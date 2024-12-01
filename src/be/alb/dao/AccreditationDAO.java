package be.alb.dao;

import be.alb.models.*;
import be.alb.database.OracleDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
            
            String query = "SELECT * FROM accreditations";
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
	
	public boolean addAccreditationsToInstructor(Instructor instructor) {
	    PreparedStatement pstmt = null;
	    Connection conn = OracleDBConnection.getInstance();
	    
	    try {
	        String accreditationQuery = "INSERT INTO InstructorAccreditation (INSTRUCTORACCREDITATIONID, INSTRUCTORID, ACCREDITATIONID) " +
	                                    "VALUES (INSTRUCTORACCREDITATION_SEQ.NEXTVAL, ?, ?)";
	        pstmt = conn.prepareStatement(accreditationQuery);

	        for (Accreditation accreditation : instructor.getAccreditations()) {
	            pstmt.setInt(1, instructor.getId());
	            pstmt.setInt(2, accreditation.getAccreditationID());
	            pstmt.addBatch(); 
	        }

	        int[] updateCounts = pstmt.executeBatch();
	        for (int count : updateCounts) {
	            if (count == 0) {
	                throw new SQLException("Failed to add accreditation to instructor.");
	            }
	        }

	        return true; 

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
