package be.alb.dao;

import be.alb.models.Lesson;

import java.sql.SQLException;
import java.util.List;

public interface LessonDAOInterface {

    // Method to create a single lesson
    boolean createLesson(Lesson lesson) throws SQLException;

    // Method to create a group of lessons
    boolean createGroupLessons(List<Lesson> lessons) throws SQLException;

    // Method to get all lessons from the database
    List<Lesson> getAllLessons() throws SQLException;

    // Method to get all private lessons from the database
    List<Lesson> getAllPrivateLessons() throws SQLException;

    // Method to get all public lessons from the database
    List<Lesson> getAllPublicLessons() throws SQLException;
}
