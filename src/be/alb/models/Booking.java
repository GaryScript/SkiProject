package be.alb.models;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Booking {

    private int bookingId;
    private Skier skier;
    private Lesson lesson;
    private Instructor instructor;

    public Booking(int bookingId, Skier skier, Lesson lesson, Instructor instructor) {
        this.bookingId = bookingId;
        this.skier = skier;
        this.lesson = lesson;
        this.instructor = instructor;
    }

    // Getters and setters
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
    
//    public static List<Skier> getAllBookings() {
//        List<Skier> skiers = null;
//
//        try {
//            skiers = BookingDAO.getAllSkiers();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return skiers;
//    }

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
}
