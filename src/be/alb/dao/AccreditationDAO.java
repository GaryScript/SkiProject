package be.alb.dao;

import be.alb.models.*;
import be.alb.database.OracleDBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AccreditationDAO {

	public static List<Accreditation> getAllAccreditations() {
        List<Accreditation> accreditations = new ArrayList<>();
        Connection conn = OracleDBConnection.getInstance();  
        Statement stmt = null;
        ResultSet rs = null;

        try {
            
            String query = "SELECT * FROM Accreditation";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);


            while (rs.next()) {
                int accreditationID = rs.getInt("AccreditationID");
                String name = rs.getString("Name");


                Accreditation accreditation = new Accreditation(accreditationID, name);
                accreditations.add(accreditation);
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

        return accreditations;  
    }
}
