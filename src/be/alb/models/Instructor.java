package be.alb.models;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import be.alb.dao.InstructorDAO;
import be.alb.utils.RegexValidator;

public class Instructor extends Person {
    private List<Accreditation> accreditations;
    private List<Lesson> lessons;
    
    public Instructor(int id, String firstName, String lastName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob) {
        super(id, firstName, lastName, city, postalCode, streetName, streetNumber, dob);
        this.accreditations = new ArrayList<>();
        this.lessons = new ArrayList<>();
    }
    
    public Instructor(int id, String firstName, String lastName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob,
    		List<Accreditation> accreditations) {
        super(id, firstName, lastName, city, postalCode, streetName, streetNumber, dob);
        this.accreditations = accreditations;
    }
    

    public void addLesson(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        if (lessons.contains(lesson)) {
            throw new IllegalStateException("Instructor already teaches this lesson");
        }

        lessons.add(lesson);
    }

    public void removeLesson(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null.");
        }

        if (!lessons.contains(lesson)) {
            throw new IllegalStateException("The instructor doesn't teach this lesson.");
        }

        lessons.remove(lesson);
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
     
    public static List<Instructor> getAllInstructors() {
    	// should not be a static dao
        return InstructorDAO.getAllInstructors();
    }
    
    public static List<String> createInstructor(String firstName, String lastName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob, List<Accreditation> accreditations) {
        List<String> result = new ArrayList<>();

        if (!RegexValidator.isValidName(firstName)) result.add("Prénom invalide.");
        if (!RegexValidator.isValidName(lastName)) result.add("Nom invalide.");
        if (!RegexValidator.isValidCity(city)) result.add("Ville invalide.");
        if (!RegexValidator.isValidPostalCode(postalCode)) result.add("Code postal invalide.");
        if (!RegexValidator.isValidStreetName(streetName)) result.add("Nom de rue invalide.");
        if (!RegexValidator.isValidStreetNumber(streetNumber)) result.add("Numéro de rue invalide.");
        if (!RegexValidator.isValidDob(dob)) result.add("Date de naissance invalide. L'instructeur doit avoir au moins 18 ans.");
        if (accreditations == null || accreditations.isEmpty()) {
            result.add("L'instructeur doit avoir au moins une accréditation.");
            return result;
        }
 
        if (!result.isEmpty()) {
            result.add(0, "0");
            return result;
        }

        Instructor newInstructor = new Instructor(0, firstName, lastName, city, postalCode, streetName, streetNumber, dob, accreditations);
        int newInstructorId = InstructorDAO.createInstructor(newInstructor);

        if (newInstructorId == -1) {
            result.add(0, "0");
            result.add("Erreur de base de données. Impossible de créer l'instructeur.");
            return result;
        }

        result.add(0, "1");
        return result;
    }
    
    public static List<Instructor> getAvailableInstructors(Date startDate, Date endDate, int lessonTypeId, boolean isPrivate)
    {
    	
    	List<Instructor> instructors = new ArrayList<>();
    	InstructorDAO instructorDAO = new InstructorDAO();
    	instructors = instructorDAO.getAvailableInstructors(startDate, endDate, lessonTypeId, isPrivate);
    	
    	return instructors;
    }   
    
    public boolean deleteInstructor() {
        return InstructorDAO.deleteInstructor(this);
    }
    
    @Override
    public boolean equals(Object obj) {
    	return this.toString()==obj.toString() ;
    }
    	
    @Override
    public int hashCode() {
    	return toString().hashCode();
    }


}
