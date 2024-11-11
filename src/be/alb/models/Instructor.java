package be.alb.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import be.alb.dao.InstructorDAO;

public class Instructor extends Person {
    private List<Accreditation> accreditations;
    private List<Lesson> lessons;

    public Instructor(int id, String name, String firstName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob) {
        super(id, name, firstName, city, postalCode, streetName, streetNumber, dob);
        this.accreditations = new ArrayList<>();
        this.lessons = new ArrayList<>();
    }

    public void addLesson(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        if (lessons.contains(lesson)) {
            throw new IllegalStateException("Instructor already teaches this lesson");
        }

//        if (!hasAccreditationForLesson(lesson)) {
//            throw new IllegalStateException("This instructor hasn't the accreditation for this lesson");
//        }

        lessons.add(lesson);
    }

    // method to supress the lesson
    public void removeLesson(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null.");
        }

        if (!lessons.contains(lesson)) {
            throw new IllegalStateException("The instructor doesn't teach this lesson.");
        }

        lessons.remove(lesson);
    }

    // check if the instructor has the accreditation for this lesson 
//    public boolean hasAccreditationForLesson(Lesson lesson) {
//        return accreditations.stream()
//                .anyMatch(accreditation -> accreditation.matchesLessonType(lesson.getLessonType()));
//    }

    // call the dao to get all instructors 
    public static List<Instructor> getAllInstructors() {
        return InstructorDAO.getAllInstructors();
    }

    public List<Accreditation> getAccreditations() {
        return accreditations;
    }

    public void setAccreditations(List<Accreditation> accreditations) {
        this.accreditations = accreditations;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}
