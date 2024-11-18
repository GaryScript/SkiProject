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

    
    public static int createInstructor(Instructor instructor) {
        Connection conn = OracleDBConnection.getInstance();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Insertion de l'instructeur dans la table
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

            // Maintenant, obtenir l'ID de l'instructeur en utilisant une requête SELECT
            String selectQuery = "SELECT INSTRUCTORID FROM Instructors WHERE lastName = ? AND firstName = ? AND dob = ?";
            pstmt = conn.prepareStatement(selectQuery);
            pstmt.setString(1, instructor.getLastName());
            pstmt.setString(2, instructor.getFirstName());
            pstmt.setDate(3, Date.valueOf(instructor.getDob()));  // Utilise la même date de naissance pour identifier l'instructeur

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("INSTRUCTORID"); // Retourner l'ID de l'instructeur nouvellement créé
            } else {
                throw new SQLException("Instructor creation failed, no ID found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
