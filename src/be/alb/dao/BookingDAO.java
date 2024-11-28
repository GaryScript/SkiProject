package be.alb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.alb.database.OracleDBConnection;
import be.alb.models.Booking;
import be.alb.models.Lesson;
import be.alb.models.Skier;

public class BookingDAO {
	public boolean isSkierAlreadyBooked(Skier skier, Lesson lesson) throws SQLException {
	    boolean isBooked = false;
	    String query = "SELECT COUNT(*) FROM Bookings WHERE skierId = ? AND lessonId = ?";
	    
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
	
	public static boolean insertBooking(Booking booking) {
	    String query = "INSERT INTO bookings (lessonid, skierid, instructorid, periodid) VALUES (?, ?, ?, ?)";

	    try (PreparedStatement stmt = OracleDBConnection.getInstance().prepareStatement(query)) {
	        stmt.setInt(1, booking.getLesson().getLessonId());
	        stmt.setInt(2, booking.getSkier().getId());
	        stmt.setInt(3, booking.getInstructor().getId());

	        if (booking.getPeriod() != null) {
	            stmt.setInt(4, booking.getPeriod().getPeriodId());
	        } else {
	            stmt.setNull(4, java.sql.Types.INTEGER);
	        }

	        int rowsInserted = stmt.executeUpdate();
	        return rowsInserted > 0; 
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


}
