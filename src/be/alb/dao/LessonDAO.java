package be.alb.dao;

import be.alb.database.OracleDBConnection;
import be.alb.models.Lesson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LessonDAO {

    // Create a single lesson
    public static boolean createLesson(Lesson lesson) {
        String query = "INSERT INTO LESSONS (MINBOOKINGS, MAXBOOKINGS, LESSONTYPEID, INSTRUCTORID, STARTDATE, ENDDATE, ISPRIVATE) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = OracleDBConnection.getInstance();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, 0); // Default for private lessons
            stmt.setInt(2, 0); // Default for private lessons
            stmt.setInt(3, lesson.getLessonType().getLessonTypeId());
            stmt.setInt(4, lesson.getInstructor() != null ? lesson.getInstructor().getId() : null);
            stmt.setDate(5, lesson.getStartDate());
            stmt.setDate(6, lesson.getEndDate());
            stmt.setBoolean(7, lesson.isPrivate());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create a group of lessons
    public static boolean createGroupLessons(List<Lesson> lessons) {
        String query = "INSERT INTO LESSONS (MINBOOKINGS, MAXBOOKINGS, LESSONTYPEID, INSTRUCTORID, STARTDATE, ENDDATE, ISPRIVATE) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = OracleDBConnection.getInstance();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            for (Lesson lesson : lessons) {
                stmt.setInt(1, 5); // Minimum bookings for group lessons
                stmt.setInt(2, 10); // Maximum bookings for group lessons
                stmt.setInt(3, lesson.getLessonType().getLessonTypeId());
                stmt.setInt(4, lesson.getInstructor() != null ? lesson.getInstructor().getId() : null);
                stmt.setDate(5, lesson.getStartDate());
                stmt.setDate(6, lesson.getEndDate());
                stmt.setBoolean(7, lesson.isPrivate());
                stmt.addBatch();
            }

            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
