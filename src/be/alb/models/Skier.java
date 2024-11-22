package be.alb.models;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import be.alb.dao.*;

public class Skier extends Person {

    private List<Booking> bookings;

    // Constructor
    public Skier(int id, String firstName, String lastName, String city, String postalCode,
                 String streetName, String streetNumber, LocalDate dob) {
        super(id, firstName, lastName, city, postalCode, streetName, streetNumber, dob);
        this.bookings = new ArrayList<>();
    }

    // Getter for bookings
    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) throws IllegalArgumentException {
        if (booking == null) {
            throw new IllegalArgumentException("La réservation ne peut pas être null.");
        }
        if (bookings.contains(booking)) {
            throw new IllegalArgumentException("La réservation existe déjà.");
        }
        bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("La réservation à supprimer ne peut pas être null.");
        }
        if (!bookings.contains(booking)) {
            throw new IllegalArgumentException("Réservation non trouvée dans la liste des réservations.");
        }
        bookings.remove(booking);
    }
    
    public static List<Skier> getAllSkiers() {
        List<Skier> skiers = null;

        try {
            skiers = skierDAO.getAllSkiers();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return skiers;
    }
}
