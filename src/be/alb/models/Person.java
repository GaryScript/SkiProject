package be.alb.models;

import java.time.LocalDate;

public abstract class Person {
	
	// variables
	private int id;
	private String name;
	private String firstName;
	private String city;
	private String postalCode;
	private String streetName;
	private String streetNumber;
	private LocalDate dob; 
	
	// constructor
	
	public Person(int id, String name, String firstName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob) {
		this.id = id;
		this.name = name; 
		this.firstName = name; 
		this.city = city;
		this.postalCode = postalCode;
		this.streetName = streetName;
		this.streetNumber = streetNumber;
		this.dob = dob;
	}
	
	// getter and setter
}
