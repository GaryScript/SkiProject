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
	 public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getFirstName() {
	        return firstName;
	    }

	    public void setFirstName(String firstName) {
	        this.firstName = firstName;
	    }

	    public String getCity() {
	        return city;
	    }

	    public void setCity(String city) {
	        this.city = city;
	    }

	    public String getPostalCode() {
	        return postalCode;
	    }

	    public void setPostalCode(String postalCode) {
	        this.postalCode = postalCode;
	    }

	    public String getStreetName() {
	        return streetName;
	    }

	    public void setStreetName(String streetName) {
	        this.streetName = streetName;
	    }

	    public String getStreetNumber() {
	        return streetNumber;
	    }

	    public void setStreetNumber(String streetNumber) {
	        this.streetNumber = streetNumber;
	    }

	    public LocalDate getDob() {
	        return dob;
	    }

	    public void setDob(LocalDate dob) {
	        this.dob = dob;
	    }
}
