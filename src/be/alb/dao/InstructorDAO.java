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
	        String query = "SELECT * FROM Instructors";
	        stmt = conn.createStatement();
	        rs = stmt.executeQuery(query);

	        while (rs.next()) {
	            int id = rs.getInt("instructorId");
	            String name = rs.getString("lastName");
	            String firstName = rs.getString("firstName");
	            String city = rs.getString("city");
	            String postalCode = rs.getString("postalCode");
	            String streetName = rs.getString("streetName");
	            String streetNumber = rs.getString("streetNumber");
	            Date dob = rs.getDate("dob");

	            LocalDate localDob = (dob != null) ? dob.toLocalDate() : null;

	            Instructor instructor = new Instructor(id, name, firstName, city, postalCode, streetName, streetNumber, localDob);
	            instructors.add(instructor);
	        }
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

    
	public static int createInstructor(Instructor instructor, List<Integer> accreditationIds) {
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
            } else {
                throw new SQLException("Instructor creation failed, no ID found.");
            }

            // add accreditations
            boolean accreditationSuccess = AccreditationDAO.addAccreditationsToInstructor(conn, instructorId, accreditationIds);
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
	        // Leçon privée: l'instructeur doit être libre à la startDate
	        sql += "AND (l.STARTDATE IS NULL OR l.STARTDATE != ?) ";
	    } else {
	        // Leçon collective: vérifier les chevauchements des dates
	        sql += "AND (l.STARTDATE IS NULL OR " +
	               "(l.ENDDATE <= ? OR l.STARTDATE >= ? OR " +
	               "(l.STARTDATE < ? AND l.ENDDATE > ?))) ";
	    }

	    sql += "ORDER BY i.LASTNAME, i.FIRSTNAME";

	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setInt(1, lessonTypeId);

	        // Si c'est une leçon privée, vérifier la seule startDate
	        if (isPrivateLesson) {
	            stmt.setDate(2, new java.sql.Date(startDate.getTime()));
	        } else {
	            // Leçon collective: vérifier les deux dates (startDate et endDate)
	            stmt.setDate(2, new java.sql.Date(endDate.getTime())); // Date de fin
	            stmt.setDate(3, new java.sql.Date(startDate.getTime())); // Date de début
	            stmt.setDate(4, new java.sql.Date(startDate.getTime())); // Date de début de la leçon à vérifier
	            stmt.setDate(5, new java.sql.Date(endDate.getTime()));   // Date de fin de la leçon à vérifier
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





}
