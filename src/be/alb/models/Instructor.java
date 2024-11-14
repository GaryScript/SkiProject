package be.alb.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import be.alb.dao.InstructorDAO;
import be.alb.utils.RegexValidator;

public class Instructor extends Person {
	// variables
	
    private List<Accreditation> accreditations;
    private List<Lesson> lessons;

    // constructors
    public Instructor(int id, String firstName, String lastName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob,
    		List<Accreditation> accreditations, List<Lesson> lessons) {
        super(id, firstName, lastName, city, postalCode, streetName, streetNumber, dob);
        this.accreditations = accreditations;
        this.lessons = lessons;
    }
    
    public Instructor(int id, String firstName, String lastName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob) {
        super(id, firstName, lastName, city, postalCode, streetName, streetNumber, dob);
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
    
    public Object createInstructor(int id, String firstName, String lastName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob) {
        List<String> errors = new ArrayList<>();

        if (!RegexValidator.isValidName(firstName)) {
            errors.add("Invalid first name.");
        }
        
        if (!RegexValidator.isValidName(lastName)) {
            errors.add("Invalid last name.");
        }

        if (!RegexValidator.isValidCity(city)) {
            errors.add("Invalid city.");
        }

        if (!RegexValidator.isValidPostalCode(postalCode)) {
            errors.add("Invalid postal code.");
        }

        if (!RegexValidator.isValidStreetName(streetName)) {
            errors.add("Invalid street name.");
        }
        
        if (!RegexValidator.isValidStreetNumber(streetNumber)) {
            errors.add("Invalid street number.");
        }
        
        if (!RegexValidator.isValidDob(dob)) {
            errors.add("Invalid dob. Instructor must be at least 18 years old.");
        }
        
        // if there are some errors --> return errors
        if (!errors.isEmpty()) {
            return errors;
        }
        
        // if no error we create the obj instructor
        Instructor newInstructor = new Instructor(id, firstName, lastName, city, postalCode, streetName, streetNumber, dob);
        
        int newInstructorId = InstructorDAO.createInstructor(newInstructor);
        
        if (newInstructorId == -1) {  // if something goes wrong in the dao
            errors.add("Database error: unable to create instructor.");
            return errors; // return errors
        }

        // Return the id of the new instructor so the app displays the page of the new instructor the user
        return newInstructorId;
    }

}
