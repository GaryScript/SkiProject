package be.alb.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Instructor extends Person {
	private List<Accreditation> accreditations;
	private List<Lesson> lessons;
	
	public Instructor(int id, String name, String firstName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob) {
        super(id, name, firstName, city, postalCode, streetName, streetNumber, dob);
        this.accreditations = new ArrayList<>(); 
        this.lessons = new ArrayList<>(); //
    }
	
	
}
