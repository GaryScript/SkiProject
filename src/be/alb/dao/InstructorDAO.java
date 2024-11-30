package be.alb.dao;

import be.alb.models.*;
import be.alb.database.OracleDBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructorDAO {

	public static List<Instructor> getAllInstructors() {
	    List<Instructor> instructors = new ArrayList<>();
	    Connection conn = OracleDBConnection.getInstance(); 
	    Statement stmt = null;
	    ResultSet rs = null;

	    try {
	        String query = "SELECT i.INSTRUCTORID, i.FIRSTNAME, i.LASTNAME, i.CITY, i.POSTALCODE, i.STREETNAME, i.STREETNUMBER, i.DOB, " +
	                       "a.ACCREDITATIONID, a.NAME AS ACCREDITATION_NAME " +
	                       "FROM INSTRUCTORS i " +
	                       "LEFT JOIN INSTRUCTORACCREDITATION ia ON i.INSTRUCTORID = ia.INSTRUCTORID " +
	                       "LEFT JOIN ACCREDITATIONS a ON ia.ACCREDITATIONID = a.ACCREDITATIONID";
	        
	        stmt = conn.createStatement();
	        rs = stmt.executeQuery(query);

	        // Using map to avoid clone
	        Map<Integer, Instructor> instructorMap = new HashMap<>();

	        while (rs.next()) {
	            int id = rs.getInt("INSTRUCTORID");
	            String name = rs.getString("LASTNAME");
	            String firstName = rs.getString("FIRSTNAME");
	            String city = rs.getString("CITY");
	            String postalCode = rs.getString("POSTALCODE");
	            String streetName = rs.getString("STREETNAME");
	            String streetNumber = rs.getString("STREETNUMBER");
	            Date dob = rs.getDate("DOB");
	            LocalDate localDob = (dob != null) ? dob.toLocalDate() : null;

	            // if instructor not alrady in map -> we add him
	            Instructor instructor = instructorMap.get(id);
	            if (instructor == null) {
	                instructor = new Instructor(id, name, firstName, city, postalCode, streetName, streetNumber, localDob, new ArrayList<>());
	                instructorMap.put(id, instructor);
	            }

	            int accreditationId = rs.getInt("ACCREDITATIONID");
	            if (accreditationId > 0) {
	                String accreditationName = rs.getString("ACCREDITATION_NAME");
	                Accreditation accreditation = new Accreditation(accreditationId, accreditationName);
	                instructor.getAccreditations().add(accreditation);
	            }
	        }
	        
	        instructors.addAll(instructorMap.values());

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    return instructors;
	}


    
	public static int createInstructor(Instructor instructor) {
        Connection conn = OracleDBConnection.getInstance();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn.setAutoCommit(false); // Disable the automatic commit (so we can rollback)

            // first step: add the instructor
            String query = "INSERT INTO Instructors (instructorid, lastName, firstName, city, postalCode, streetName, streetNumber, dob) " +
                           "VALUES (INSTRUCTOR_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, instructor.getLastName());
            pstmt.setString(2, instructor.getFirstName());
            pstmt.setString(3, instructor.getCity());
            pstmt.setString(4, instructor.getPostalCode());
            pstmt.setString(5, instructor.getStreetName());
            pstmt.setString(6, instructor.getStreetNumber());
            pstmt.setDate(7, Date.valueOf(instructor.getDob()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating instructor failed, no rows affected.");
            }

            // get the id of the new instructor
            String selectQuery = "SELECT INSTRUCTORID FROM Instructors WHERE lastName = ? AND firstName = ? AND dob = ?";
            pstmt = conn.prepareStatement(selectQuery);
            pstmt.setString(1, instructor.getLastName());
            pstmt.setString(2, instructor.getFirstName());
            pstmt.setDate(3, Date.valueOf(instructor.getDob()));

            rs = pstmt.executeQuery();
            int instructorId = -1;
            if (rs.next()) {
                instructorId = rs.getInt("INSTRUCTORID");
                instructor.setId(instructorId);
            } else {
                throw new SQLException("Instructor creation failed, no ID found.");
            }
           
            // add accreditations
            boolean accreditationSuccess = Accreditation.addAccreditationsToInstructor(instructor);
            if (!accreditationSuccess) {
                throw new SQLException("Failed to add accreditations to instructor.");
            }

            // commit if everything went fine
            conn.commit();

            return instructorId;

        } catch (SQLException e) {
            // if error -> rollback
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return -1;

        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // re active the commit
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	public List<Instructor> getAvailableInstructors(Date startDate, Date endDate, int lessonTypeId, boolean isPrivateLesson) {
	    List<Instructor> availableInstructors = new ArrayList<>();
	    Connection connection = OracleDBConnection.getInstance();

	    // Définir la requête SQL de base
	    String sql = "SELECT DISTINCT i.INSTRUCTORID, i.FIRSTNAME, i.LASTNAME, i.CITY, i.POSTALCODE, i.STREETNAME, i.STREETNUMBER, i.DOB, " +
	                 "a.ACCREDITATIONID, a.NAME AS ACCREDITATION_NAME " +
	                 "FROM INSTRUCTORS i " +
	                 "JOIN INSTRUCTORACCREDITATION ia ON i.INSTRUCTORID = ia.INSTRUCTORID " +
	                 "JOIN ACCREDITATIONS a ON ia.ACCREDITATIONID = a.ACCREDITATIONID " +
	                 "JOIN LESSONTYPE lt ON a.ACCREDITATIONID = lt.ACCREDITATIONID " +
	                 "LEFT JOIN LESSONS l ON i.INSTRUCTORID = l.INSTRUCTORID " +
	                 "WHERE lt.LESSONTYPEID = ? " +
	                 "AND a.ACCREDITATIONID = lt.ACCREDITATIONID ";

	    if (isPrivateLesson) {
	        // private lesson -> just need to be freed at 12 this day
	        sql += "AND (l.STARTDATE IS NULL OR l.STARTDATE != ?) ";
	    } else {
	        // check if not chevauching lesson
	        sql += "AND (l.STARTDATE IS NULL OR " +
	               "(l.ENDDATE <= ? OR l.STARTDATE >= ? OR " +
	               "(l.STARTDATE < ? AND l.ENDDATE > ?))) ";
	    }

	    sql += "ORDER BY i.LASTNAME, i.FIRSTNAME";

	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setInt(1, lessonTypeId);

	        if (isPrivateLesson) {
	            stmt.setDate(2, new java.sql.Date(startDate.getTime()));
	        } else {
	            // check both date
	            stmt.setDate(2, new java.sql.Date(endDate.getTime())); 
	            stmt.setDate(3, new java.sql.Date(startDate.getTime())); 
	            stmt.setDate(4, new java.sql.Date(startDate.getTime())); 
	            stmt.setDate(5, new java.sql.Date(endDate.getTime()));  
	        }

	        try (ResultSet rs = stmt.executeQuery()) {
	            Map<Integer, Instructor> instructorMap = new HashMap<>();

	            while (rs.next()) {
	                int instructorId = rs.getInt("INSTRUCTORID");

	                if (!instructorMap.containsKey(instructorId)) {
	                    Instructor instructor = new Instructor(
	                        instructorId,
	                        rs.getString("FIRSTNAME"),
	                        rs.getString("LASTNAME"),
	                        rs.getString("CITY"),
	                        rs.getString("POSTALCODE"),
	                        rs.getString("STREETNAME"),
	                        rs.getString("STREETNUMBER"),
	                        rs.getDate("DOB").toLocalDate(),
	                        new ArrayList<>()
	                    );
	                    instructorMap.put(instructorId, instructor);
	                }

	                Accreditation accreditation = new Accreditation(
	                    rs.getInt("ACCREDITATIONID"),
	                    rs.getString("ACCREDITATION_NAME")
	                );
	                instructorMap.get(instructorId).getAccreditations().add(accreditation);
	            }

	            availableInstructors.addAll(instructorMap.values());
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return availableInstructors;
	}
	
	public static boolean deleteInstructor(Instructor instructor) {
        Connection conn = OracleDBConnection.getInstance();
        PreparedStatement pstmt = null;
        
        try {
            String deleteInstructorQuery = "DELETE FROM instructors WHERE instructorid = ?";
            pstmt = conn.prepareStatement(deleteInstructorQuery);
            pstmt.setInt(1, instructor.getId());
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0; 

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }





}
