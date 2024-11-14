package be.alb.dao;

import be.alb.models.*;
import be.alb.database.OracleDBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

                Instructor instructor = new Instructor(id, name, firstName, city, postalCode, streetName, streetNumber, dob.toLocalDate());
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
        ResultSet generatedKeys = null;
        
        try {
            String query = "INSERT INTO Instructors (lastName, firstName, city, postalCode, streetName, streetNumber, dob) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
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

            generatedKeys = pstmt.getGeneratedKeys(); // this function returns the primary key that has been just created, really usefull to view the new instructor we've just been created
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); 
            } else {
                throw new SQLException("Creating instructor failed, no ID obtained.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // means something went wrong
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
