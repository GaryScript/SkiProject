package be.alb.dao;

import be.alb.models.Booking;
import be.alb.models.Lesson;
import be.alb.models.Skier;

import java.sql.SQLException;
import java.util.List;

public interface BookingDAOInterface {
    boolean isSkierAlreadyBooked(Skier skier, Lesson lesson) throws SQLException;


    boolean insertBooking(Booking booking);


    List<Booking> getAllBookings();
}