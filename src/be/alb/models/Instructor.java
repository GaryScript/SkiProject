package be.alb.models;
import java.time.LocalDate;
import be.alb.dao.InstructorDAO;
import java.util.ArrayList;
import java.util.List;

public class Instructor extends Person {
	private int id;
	private List<Accreditation> accreditations;
	private List<Lesson> lessons;
	
	public Instructor(int id, String name, String firstName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob) {
        super(id, name, firstName, city, postalCode, streetName, streetNumber, dob);
        this.accreditations = new ArrayList<>(); 
        this.lessons = new ArrayList<>(); //
    }
	
	public void AddLesson(Lesson lesson)
	{
		if(lesson == null)
			throw new IllegalArgumentException();
		
		if(lessons.contains(lesson) == true)
			throw new IllegalStateException();
		
		lessons.add(lesson);
		
//		boolean hasAccreditation = accreditations.stream()
//		        .anyMatch(accreditation -> accreditation.matchesLessonType(lesson.getLessonType()));
//		
//		
		// to do : verif accreditation, might be a seperated function actually
		
		// might need more verif
		// might need a return ?
	}
	
	public void RemoveLesson(Lesson lesson)
	{
		if(lesson == null)
			throw new IllegalArgumentException();
		
		if(lessons.contains(lesson) == false)
			throw new IllegalStateException();
		
		lessons.remove(lesson);
		
		// might need more verif
		// might need a return ?
	}
	
	public static List<Instructor> getAllInstructors() {
        return InstructorDAO.getAllInstructors();
    }
	
	
}
