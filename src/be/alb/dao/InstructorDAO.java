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
import java.util.List;

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
            // Démarrer une transaction
            conn.setAutoCommit(false); // Désactive le commit automatique

            // Insertion de l'instructeur
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

            // Obtenir l'ID de l'instructeur
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

            // Appeler la méthode d'ajout des accréditations
            boolean accreditationSuccess = AccreditationDAO.addAccreditationsToInstructor(conn, instructorId, accreditationIds);
            if (!accreditationSuccess) {
                throw new SQLException("Failed to add accreditations to instructor.");
            }

            // Commit de la transaction si tout est ok
            conn.commit();

            return instructorId; // Retourner l'ID de l'instructeur nouvellement créé

        } catch (SQLException e) {
            // En cas d'erreur, rollback de la transaction
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
                    conn.setAutoCommit(true); // Réactive le commit automatique après la transaction
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
