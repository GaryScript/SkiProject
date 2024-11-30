package be.alb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

	    try (Connection conn = OracleDBConnection.getInstance()) {
	        String query = """
	            SELECT b.BOOKINGID, b.LESSONID, b.SKIERID, b.INSTRUCTORID, b.PERIODID,
	                   p.STARTDATE, p.ENDDATE, p.ISVACATION,
	                   l.LESSONID, l.STARTDATE AS LESSON_STARTDATE, l.ENDDATE AS LESSON_ENDDATE, 
	                   l.ISPRIVATE, l.LESSONTYPEID, lt.NAME AS LESSONTYPENAME, 
	                   lt.AGEGROUP, lt.SPORTTYPE, lt.PRICE, lt.ACCREDITATIONID,
	                   i.INSTRUCTORID, i.LASTNAME AS INSTRUCTOR_LASTNAME, i.FIRSTNAME AS INSTRUCTOR_FIRSTNAME, 
	                   a.ACCREDITATIONID, a.NAME AS ACCREDITATION_NAME,
	                   s.SKIERID, s.LASTNAME AS SKIER_LASTNAME, s.FIRSTNAME AS SKIER_FIRSTNAME, s.HASINSURANCE
	            FROM BOOKINGS b
	            LEFT JOIN PERIODS p ON b.PERIODID = p.PERIODID
	            LEFT JOIN LESSONS l ON b.LESSONID = l.LESSONID
	            LEFT JOIN LESSONTYPES lt ON l.LESSONTYPEID = lt.LESSONTYPEID
	            LEFT JOIN INSTRUCTORS i ON b.INSTRUCTORID = i.INSTRUCTORID
	            LEFT JOIN INSTRUCTORACCREDITATIONS ia ON i.INSTRUCTORID = ia.INSTRUCTORID
	            LEFT JOIN ACCREDITATIONS a ON ia.ACCREDITATIONID = a.ACCREDITATIONID
	            LEFT JOIN SKIERS s ON b.SKIERID = s.SKIERID
	        """;

	        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
	            Map<Integer, Period> periodMap = new HashMap<>();
	            Map<Integer, Accreditation> accreditationMap = new HashMap<>();
	            Map<Integer, Instructor> instructorMap = new HashMap<>();
	            Map<Integer, LessonType> lessonTypeMap = new HashMap<>();
	            Map<Integer, Lesson> lessonMap = new HashMap<>();
	            Map<Integer, Skier> skierMap = new HashMap<>();

	            while (rs.next()) {
	                int periodId = rs.getInt("PERIODID");
	                Period period = periodMap.computeIfAbsent(periodId, id -> {
						try {
							return new Period(
							    id,
							    rs.getDate("STARTDATE"),
							    rs.getDate("ENDDATE"),
							    rs.getInt("ISVACATION") == 1
							);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					});

	                int accreditationId = rs.getInt("ACCREDITATIONID");
	                Accreditation accreditation = accreditationMap.computeIfAbsent(accreditationId, id -> {
						try {
							return new Accreditation(
							    id,
							    rs.getString("ACCREDITATION_NAME")
							);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					});

	                int instructorId = rs.getInt("INSTRUCTORID");
	                Instructor instructor = instructorMap.computeIfAbsent(instructorId, id -> {
	                    List<Accreditation> accreditations = new ArrayList<>();
	                    accreditations.add(accreditation);
	                    try {
							return new Instructor(
							    id,
							    rs.getString("INSTRUCTOR_FIRSTNAME"),
							    rs.getString("INSTRUCTOR_LASTNAME"),
							    rs.getString("CITY"),
							    rs.getString("POSTALCODE"),
							    rs.getString("STREETNAME"),
							    rs.getString("STREETNUMBER"),
							    rs.getDate("DOB").toLocalDate(),
							    accreditations
							);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
	                });

	                int lessonTypeId = rs.getInt("LESSONTYPEID");
	                LessonType lessonType = lessonTypeMap.computeIfAbsent(lessonTypeId, id -> {
						try {
							return new LessonType(
							    id,
							    rs.getString("LESSONTYPENAME"),
							    rs.getString("AGEGROUP"),
							    rs.getString("SPORTTYPE"),
							    rs.getDouble("PRICE"),
							    accreditation
							);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					});

	                int lessonId = rs.getInt("LESSONID");
	                Lesson lesson = lessonMap.computeIfAbsent(lessonId, id -> {
						try {
							return new Lesson(
							    id,
							    rs.getDate("LESSON_STARTDATE"),
							    rs.getDate("LESSON_ENDDATE"),
							    instructor,
							    lessonType,
							    rs.getInt("ISPRIVATE") == 1
							);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					});

	                int skierId = rs.getInt("SKIERID");
	                Skier skier = skierMap.computeIfAbsent(skierId, id -> {
						try {
							return new Skier(
							    id,
							    rs.getString("SKIER_FIRSTNAME"),
							    rs.getString("SKIER_LASTNAME"),
							    rs.getString("CITY"),
							    rs.getString("POSTALCODE"),
							    rs.getString("STREETNAME"),
							    rs.getString("STREETNUMBER"),
							    rs.getDate("DOB").toLocalDate(),
							    rs.getInt("HASINSURANCE") == 1
							);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					});

	                bookings.add(new Booking(
	                    rs.getInt("BOOKINGID"),
	                    skier,
	                    lesson,
	                    instructor,
	                    period
	                ));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return bookings;
	}




}
