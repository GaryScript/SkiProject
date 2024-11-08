package be.alb.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Skier extends Person {
	private List<Booking> bookings; 

    // Constructor
    public Skier(int id, String name, String firstName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob) {
        super(id, name, firstName, city, postalCode, streetName, streetNumber, dob);
        this.bookings = new ArrayList<>();
    }
}
