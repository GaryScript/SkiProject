package be.alb.controllers;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import be.alb.models.*;
import be.alb.utils.RegexValidator;

public class InstructorController {
	
	public List<Instructor> getAllInstructors() {
        return Instructor.getAllInstructors();
    }
	
	public List<String> createInstructor(int id, String firstName, String lastName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob) {
	    List<String> errors = new ArrayList<>();
	    
	    if (!(RegexValidator.isValidName(firstName))) {
	        errors.add("Invalid first name.");
	    }
	    
	    if (!(RegexValidator.isValidName(lastName))) {
	        errors.add("Invalid last name.");
	    }

	    if (!(RegexValidator.isValidCity(city))) {
	        errors.add("Invalid last name.");
	    }

	    if (!(RegexValidator.isValidPostalCode(postalCode))) {
	        errors.add("Invalid postal code.");
	    }

	    if (!(RegexValidator.isValidStreetName(streetName))) {
	        errors.add("Invalid street name.");
	    }
	    
	    if (!(RegexValidator.isValidStreetNumber(streetNumber))) {
	        errors.add("Invalid last name.");
	    }
	    
	    if (!(RegexValidator.isValidDob(dob))) {
	        errors.add("Invalid dob. Instructor cannot be less than 18 years.");
	    }
	    
	    if(errors.isEmpty())
	    	Instructor.createInstructor(int id, String firstName, String lastName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob)
	    return errors; 
	}
}
