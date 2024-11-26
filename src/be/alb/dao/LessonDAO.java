package be.alb.dao;

import be.alb.database.OracleDBConnection;
import be.alb.models.Lesson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class LessonDAO {

    // Create a single lesson
    public static boolean createLesson(Lesson lesson) {
        String query = "INSERT INTO LESSONS (LESSONID, MINBOOKINGS, MAXBOOKINGS, LESSONTYPEID, INSTRUCTORID, STARTDATE, ENDDATE, ISPRIVATE) "
                     + "VALUES (Lessons_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = OracleDBConnection.getInstance();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Set parameters for the prepared statement
            stmt.setInt(1, lesson.getMinBookings());
            stmt.setInt(2, lesson.getMaxBookings());
            stmt.setInt(3, lesson.getLessonType().getLessonTypeId());
            
            // Handle nullable instructor ID
            if (lesson.getInstructor() != null) {
                stmt.setInt(4, lesson.getInstructor().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setDate(5, lesson.getStartDate());
            stmt.setDate(6, lesson.getEndDate());
            stmt.setInt(7, lesson.isPrivate() ? 1 : 0);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create a group of lessons
    public static boolean createGroupLessons(List<Lesson> lessons) {
        String query = "INSERT INTO LESSONS (LESSONID, MINBOOKINGS, MAXBOOKINGS, LESSONTYPEID, INSTRUCTORID, STARTDATE, ENDDATE, ISPRIVATE) "
                     + "VALUES (Lessons_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = OracleDBConnection.getInstance();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            for (Lesson lesson : lessons) {
                stmt.setInt(1, lesson.getMinBookings());
                stmt.setInt(2, lesson.getMaxBookings());
                stmt.setInt(3, lesson.getLessonType().getLessonTypeId());

                // Handle nullable instructor ID
                if (lesson.getInstructor() != null) {
                    stmt.setInt(4, lesson.getInstructor().getId());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }

                stmt.setDate(5, lesson.getStartDate());
                stmt.setDate(6, lesson.getEndDate());
                stmt.setInt(7, lesson.isPrivate() ? 1 : 0);

                stmt.addBatch();
                stmt.clearParameters();
            }

            // Execute batch insert
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
