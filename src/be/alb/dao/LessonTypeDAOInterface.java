package be.alb.dao;

import be.alb.models.LessonType;
import java.sql.SQLException;
import java.util.List;

public interface LessonTypeDAOInterface {
    List<LessonType> getAllLessonTypes() throws SQLException;
}
