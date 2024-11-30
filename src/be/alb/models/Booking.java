package be.alb.models;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.swing.JOptionPane;

import be.alb.dao.BookingDAO;
import be.alb.dao.LessonDAO;
import be.alb.dao.SkierDAO;

public class Booking {

    private int bookingId;
    private Skier skier;
    private Lesson lesson;
    private Instructor instructor;
    private Period period; 

    // constructor for private booking lesson
    public Booking(int bookingId, Skier skier, Lesson lesson, Instructor instructor, Period period) {
        this.bookingId = bookingId;
        this.skier = skier;
        this.lesson = lesson;
        this.instructor = instructor;
        this.period = period;
    }

    // constructor for grouped booking lesson
    public Booking(int bookingId, Skier skier, Lesson lesson, Instructor instructor) {
        this.bookingId = bookingId;
        this.skier = skier;
        this.lesson = lesson;
        this.instructor = instructor;
        this.period = null;
    }

    // Getters et Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Skier getSkier() {
        return skier;
    }

    public void setSkier(Skier skier) {
        this.skier = skier;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking booking = (Booking) obj;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
    
    public static boolean createPrivateBooking(Skier skier, Lesson lesson, Date bookingDate) {
        try {
            // Récupération de l'instructeur à partir de la leçon
            Instructor instructor = lesson.getInstructor();

            // Vérifier si la réservation est encore possible
            Period period = Period.getPeriodForDate(lesson.getStartDate());
            if (!isEligibleForBooking(period, bookingDate)) {
                JOptionPane.showMessageDialog(null, "Booking not eligible: date is out of allowed range.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Vérifier si le cours n'a pas atteint le maximum d'inscriptions
            if (lesson.isLessonFull()) {
                JOptionPane.showMessageDialog(null, "Booking not eligible: lesson has reached max bookings.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Vérifier si le skieur est déjà inscrit au cours
            if (Booking.isSkierAlreadyBooked(skier, lesson)) {
                JOptionPane.showMessageDialog(null, "Booking not eligible: this skier already has a booking for this class.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Vérifier si l'âge du skieur est valide pour ce type de cours
            if (!Booking.isAgeValidForLesson(skier, lesson)) {
                JOptionPane.showMessageDialog(null, "Booking not eligible: this skier is either too young or too old to book this type of lesson.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Créer l'objet Booking
            Booking booking = new Booking(0, skier, lesson, instructor, period);

            // Insérer la réservation dans la base de données
            boolean success = BookingDAO.insertBooking(booking);

            if (success) {
                JOptionPane.showMessageDialog(null, "Booking successfully created.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Booking creation failed at DAO level.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();  // Log de l'erreur
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    
    public static boolean createGroupBookings(Skier skier, List<Lesson> lessons, Date bookingDate) {
        try {
            if (lessons.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No lessons selected for booking.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Lesson firstLesson = lessons.get(0);

            // Récupération de l'instructeur à partir de la première leçon
            Instructor instructor = firstLesson.getInstructor();

            // Vérifier si la première leçon n'a pas atteint le maximum d'inscriptions
            if (firstLesson.isLessonFull()) {
                JOptionPane.showMessageDialog(null,
                    "Booking not eligible: lesson '" + firstLesson.getLessonType().getName() + "' has reached max bookings.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Vérifier si le skieur est déjà inscrit à la première leçon
            if (Booking.isSkierAlreadyBooked(skier, firstLesson)) {
                JOptionPane.showMessageDialog(null,
                    "Booking not eligible: this skier already has a booking for lesson '" + firstLesson.getLessonType().getName() + "'.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Vérifier si l'âge du skieur est valide pour la première leçon
            if (!Booking.isAgeValidForLesson(skier, firstLesson)) {
                JOptionPane.showMessageDialog(null,
                    "Booking not eligible: skier is not eligible for lesson '" + firstLesson.getLessonType().getName() + "' due to age restrictions.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Créer et insérer les réservations pour chaque leçon dans le groupe
            for (Lesson lesson : lessons) {
                Booking booking = new Booking(0, skier, lesson, instructor, null);
                boolean success = BookingDAO.insertBooking(booking);
                if (!success) {
                    JOptionPane.showMessageDialog(null,
                        "Booking creation failed for lesson '" + lesson.getLessonType().getName() + "'.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            JOptionPane.showMessageDialog(null, "All bookings successfully processed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();  // Log de l'erreur
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static boolean isEligibleForBooking(Period period, Date bookingDate) {
        java.util.Date today = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        if (period.isVacation()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        } else {
            calendar.add(Calendar.MONTH, 1);
        }

        java.util.Date limitDate = calendar.getTime();

        return !bookingDate.after(limitDate);
    }
    
    private static boolean isSkierAlreadyBooked(Skier skier, Lesson lesson)
    {
    	BookingDAO bookingDAO = new BookingDAO(); 
    	try {
            return bookingDAO.isSkierAlreadyBooked(skier, lesson);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving lessons: " + e.getMessage(), e);
        }
    }
    
    private static boolean isAgeValidForLesson(Skier skier, Lesson lesson) {
        LocalDate dob = skier.getDob(); 
        LocalDate today = LocalDate.now();

        int age = today.getYear() - dob.getYear();
        if (today.getDayOfYear() < dob.getDayOfYear()) {
            age--;
        }
        
        String lessonType = lesson.getLessonType().getAgeGroup(); 

        if (lessonType.contains("Enfant")) {
            if (age < 4 || age > 12) {
                if (lessonType.contains("Snowboard") && age < 6) {
                    return false; 
                }
                return false; 
            }
        } else if (lessonType.contains("Adulte")) {
            if (age <= 12) {
                return false;
            }
        }
        
        return true;
    }
    
    public static List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = null;
        
        BookingDAO bookingDAO = new BookingDAO();

        bookings = bookingDAO.getAllBookings();

        return bookings;
    }
    
    







}
