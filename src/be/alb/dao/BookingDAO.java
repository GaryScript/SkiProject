package be.alb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.alb.database.OracleDBConnection;
import be.alb.models.Accreditation;
import be.alb.models.Booking;
import be.alb.models.Instructor;
import be.alb.models.Lesson;
import be.alb.models.LessonType;
import be.alb.models.Period;
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
	
	public List<Booking> getAllBookings() {
	    List<Booking> bookings = new ArrayList<>();
	    Connection conn = OracleDBConnection.getInstance();
	    Statement stmt = null;
	    ResultSet rs = null;

	    try {
	        String query = """
	            SELECT b.BOOKINGID, b.LESSONID, b.SKIERID, b.INSTRUCTORID, b.PERIODID,
	                   p.STARTDATE, p.ENDDATE, p.ISVACATION,
	                   l.LESSONID, l.STARTDATE AS LESSON_STARTDATE, l.ENDDATE AS LESSON_ENDDATE, 
	                   l.ISPRIVATE, l.LESSONTYPEID, lt.NAME AS LESSONTYPENAME, 
	                   lt.AGEGROUP, lt.SPORTTYPE, lt.PRICE, lt.ACCREDITATIONID,
	                   i.INSTRUCTORID, i.LASTNAME AS INSTRUCTOR_LASTNAME, i.FIRSTNAME AS INSTRUCTOR_FIRSTNAME, i.CITY, i.POSTALCODE, I.STREETNAME, i.STREETNUMBER, i.DOB, 
	                   a.ACCREDITATIONID, a.NAME AS ACCREDITATION_NAME,
	                   s.SKIERID, s.LASTNAME AS SKIER_LASTNAME, s.FIRSTNAME AS SKIER_FIRSTNAME, s.HASINSURANCE
	            FROM BOOKINGS b
	            LEFT JOIN PERIODS p ON b.PERIODID = p.PERIODID
	            LEFT JOIN LESSONS l ON b.LESSONID = l.LESSONID
	            LEFT JOIN LESSONTYPE lt ON l.LESSONTYPEID = lt.LESSONTYPEID
	            LEFT JOIN INSTRUCTORS i ON b.INSTRUCTORID = i.INSTRUCTORID
	            LEFT JOIN INSTRUCTORACCREDITATION ia ON i.INSTRUCTORID = ia.INSTRUCTORID
	            LEFT JOIN ACCREDITATIONS a ON ia.ACCREDITATIONID = a.ACCREDITATIONID
	            LEFT JOIN SKIERS s ON b.SKIERID = s.SKIERID
	        """;

	        stmt = conn.createStatement();
	        rs = stmt.executeQuery(query);

	        Map<Integer, Period> periodMap = new HashMap<>();
	        Map<Integer, Accreditation> accreditationMap = new HashMap<>();
	        Map<Integer, Instructor> instructorMap = new HashMap<>();
	        Map<Integer, LessonType> lessonTypeMap = new HashMap<>();
	        Map<Integer, Lesson> lessonMap = new HashMap<>();
	        Map<Integer, Skier> skierMap = new HashMap<>();

	        while (rs.next()) {
	            int periodId = rs.getInt("PERIODID");
	            Period period = periodMap.get(periodId);
	            if (period == null) {
	                period = new Period(
	                    periodId,
	                    rs.getDate("STARTDATE"),
	                    rs.getDate("ENDDATE"),
	                    rs.getInt("ISVACATION") == 1
	                );
	                periodMap.put(periodId, period);
	            }

	            int accreditationId = rs.getInt("ACCREDITATIONID");
	            Accreditation accreditation = accreditationMap.get(accreditationId);
	            if (accreditation == null) {
	                accreditation = new Accreditation(
	                    accreditationId,
	                    rs.getString("ACCREDITATION_NAME")
	                );
	                accreditationMap.put(accreditationId, accreditation);
	            }

	            int instructorId = rs.getInt("INSTRUCTORID");
	            Instructor instructor = instructorMap.get(instructorId);
	            if (instructor == null) {
	                instructor = new Instructor(
	                    instructorId,
	                    rs.getString("INSTRUCTOR_FIRSTNAME"),
	                    rs.getString("INSTRUCTOR_LASTNAME"),
	                    rs.getString("CITY"),
	                    rs.getString("POSTALCODE"),
	                    rs.getString("STREETNAME"),
	                    rs.getString("STREETNUMBER"),
	                    rs.getDate("DOB").toLocalDate(),
	                    Collections.singletonList(accreditation)
	                );
	                instructorMap.put(instructorId, instructor);
	            }

	            int lessonTypeId = rs.getInt("LESSONTYPEID");
	            LessonType lessonType = lessonTypeMap.get(lessonTypeId);
	            if (lessonType == null) {
	                lessonType = new LessonType(
	                    lessonTypeId,
	                    rs.getString("LESSONTYPENAME"),
	                    rs.getString("AGEGROUP"),
	                    rs.getString("SPORTTYPE"),
	                    rs.getDouble("PRICE"),
	                    accreditation
	                );
	                lessonTypeMap.put(lessonTypeId, lessonType);
	            }

	            int lessonId = rs.getInt("LESSONID");
	            Lesson lesson = lessonMap.get(lessonId);
	            if (lesson == null) {
	                lesson = new Lesson(
	                    lessonId,
	                    rs.getDate("LESSON_STARTDATE"),
	                    rs.getDate("LESSON_ENDDATE"),
	                    instructor,
	                    lessonType,
	                    rs.getInt("ISPRIVATE") == 1
	                );
	                lessonMap.put(lessonId, lesson);
	            }

	            int skierId = rs.getInt("SKIERID");
	            Skier skier = skierMap.get(skierId);
	            if (skier == null) {
	                skier = new Skier(
	                    skierId,
	                    rs.getString("SKIER_FIRSTNAME"),
	                    rs.getString("SKIER_LASTNAME"),
	                    rs.getString("CITY"),
	                    rs.getString("POSTALCODE"),
	                    rs.getString("STREETNAME"),
	                    rs.getString("STREETNUMBER"),
	                    rs.getDate("DOB").toLocalDate(),
	                    rs.getInt("HASINSURANCE") == 1
	                );
	                skierMap.put(skierId, skier);
	            }

	            bookings.add(new Booking(
	                rs.getInt("BOOKINGID"),
	                skier,
	                lesson,
	                instructor,
	                period
	            ));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	            //if (conn != null) conn.close();  // Fermer la connexion après usage
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    return bookings;
	}








}
