package be.alb.models;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import be.alb.dao.LessonDAO;
import be.alb.enums.LessonTypeEnum;

public class Lesson {
    private int lessonId;
    private Date startDate;
    private Date endDate;
    private Instructor instructor;
    private LessonType lessonType;
    private boolean isPrivate;

    public Lesson(int lessonId, Date startDate, Date endDate, Instructor instructor, LessonType lessonType, boolean isPrivate) {
        this.lessonId = lessonId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructor = instructor;
        this.lessonType = lessonType;
        this.isPrivate = isPrivate;
    }

    // Getters and Setters
    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    // Static method to handle creation of lessons
    public static boolean createLesson(Date startDate, Date endDate, Instructor instructor, LessonType lessonType, boolean isPrivate) {
        if (isPrivate) {
            // Create a single private lesson
            Lesson privateLesson = new Lesson(0, startDate, endDate, instructor, lessonType, true);
            return LessonDAO.createLesson(privateLesson);
        } else {
            // Create group lessons (6 lessons: 3 mornings, 3 afternoons)
            List<Lesson> groupLessons = new ArrayList<>();
            long oneDay = 24 * 60 * 60 * 1000; // Milliseconds in one day
            Date currentDate = startDate;

            for (int i = 0; i < 3; i++) { // Loop for 3 days
                // Morning session
                groupLessons.add(new Lesson(
                    0,
                    new Date(currentDate.getTime() + 9 * 60 * 60 * 1000), // 9:00
                    new Date(currentDate.getTime() + 12 * 60 * 60 * 1000), // 12:00
                    instructor,
                    lessonType,
                    false
                ));
                // Afternoon session
                groupLessons.add(new Lesson(
                    0,
                    new Date(currentDate.getTime() + 14 * 60 * 60 * 1000), // 14:00
                    new Date(currentDate.getTime() + 17 * 60 * 60 * 1000), // 17:00
                    instructor,
                    lessonType,
                    false
                ));

                // Move to the next day
                currentDate = new Date(currentDate.getTime() + oneDay);
            }

            // Save all group lessons
            return LessonDAO.createGroupLessons(groupLessons);
        }
    }
}
