package be.alb.dao;

import be.alb.models.*;
import be.alb.database.OracleDBConnection;

import java.sql.Connection;
import java.sql.Date;
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
}
