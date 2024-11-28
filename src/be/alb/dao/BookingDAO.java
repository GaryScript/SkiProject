package be.alb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.alb.database.OracleDBConnection;
import be.alb.models.Lesson;
import be.alb.models.Skier;

public class BookingDAO {
	public boolean isSkierAlreadyBooked(Skier skier, Lesson lesson) throws SQLException {
	    boolean isBooked = false;
	    String query = "SELECT COUNT(*) FROM Booking WHERE skierId = ? AND lessonId = ?";
	    
	    try (PreparedStatement stmt = OracleDBConnection.getInstance().prepareStatement(query)) {
	        stmt.setInt(1, skier.getId());
	        stmt.setInt(2, lesson.getLessonId()); 
	        
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            isBooked = rs.getInt(1) > 0; 
	        }
	    }
	    
	    return isBooked;
	}

}
