package be.alb.dao;

import be.alb.models.Instructor;
import java.sql.Date;
import java.util.List;

public interface InstructorDAOInterface {
    List<Instructor> getAllInstructors();
    int createInstructor(Instructor instructor);
    List<Instructor> getAvailableInstructors(Date startDate, Date endDate, int lessonTypeId, boolean isPrivateLesson);
    boolean deleteInstructor(Instructor instructor);
}
