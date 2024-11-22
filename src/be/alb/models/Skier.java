package be.alb.models;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import be.alb.dao.*;
import be.alb.utils.RegexValidator;

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
        
        SkierDAO skierDAO = new SkierDAO();

        try {
            skiers = skierDAO.getAllSkiers();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return skiers;
    }
    
    public static List<String> createSkier(String firstName, String lastName, String city, String postalCode, String streetName, String streetNumber, LocalDate dob, int hasInsurance) {
        List<String> result = new ArrayList<>();
        SkierDAO skierDAO = new SkierDAO();

        // validations
        if (!RegexValidator.isValidName(firstName)) result.add("Prénom invalide.");
        if (!RegexValidator.isValidName(lastName)) result.add("Nom invalide.");
        if (!RegexValidator.isValidCity(city)) result.add("Ville invalide.");
        if (!RegexValidator.isValidPostalCode(postalCode)) result.add("Code postal invalide.");
        if (!RegexValidator.isValidStreetName(streetName)) result.add("Nom de rue invalide.");
        if (!RegexValidator.isValidStreetNumber(streetNumber)) result.add("Numéro de rue invalide.");
        if (!RegexValidator.isValidDobSkier(dob)) result.add("Date de naissance invalide. L'instructeur doit avoir au moins 18 ans.");

        // if there are errors, return 0 
        if (!result.isEmpty()) {
            result.add(0, "0");
            return result;
        }

        // bdd add
        Skier newSkier = new Skier(0, firstName, lastName, city, postalCode, streetName, streetNumber, dob, hasInsurance);
        int newSkierId = -1;
        try {
            newSkierId = skierDAO.createSkier(newSkier);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (newSkierId == -1) {
            result.add(0, "0");
            result.add("Erreur de base de données. Impossible de créer l'instructeur.");
            return result;
        }

        // everything went fine
        result.add(0, "1");
        return result;
    }

}
