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
	
	 static boolean addAccreditationsToInstructor(Connection conn, int instructorId, List<Integer> accreditationIds) {
	        PreparedStatement pstmt = null;

	        try {
	            String accreditationQuery = "INSERT INTO InstructorAccreditation (INSTRUCTORACCREDITATIONID, INSTRUCTORID, ACCREDITATIONID) " +
	                                        "VALUES (INSTRUCTORACCREDITATION_SEQ.NEXTVAL, ?, ?)";
	            pstmt = conn.prepareStatement(accreditationQuery);

	            for (Integer accreditationId : accreditationIds) {
	                pstmt.setInt(1, instructorId);
	                pstmt.setInt(2, accreditationId);
	                pstmt.addBatch();  // Prépare l'ajout dans un batch
	            }

	            int[] updateCounts = pstmt.executeBatch(); // Exécution du batch

	            // Vérifier si l'ajout des accréditations s'est bien passé
	            for (int count : updateCounts) {
	                if (count == 0) {
	                    throw new SQLException("Failed to add accreditation to instructor.");
	                }
	            }

	            return true; // Succès

	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false; // Erreur lors de l'ajout des accréditations
	        } finally {
	            try {
	                if (pstmt != null) pstmt.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
    }
}
