package be.alb.models;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

import be.alb.dao.BookingDAO;
import be.alb.dao.LessonDAO;

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
    
    public static boolean createPrivateBooking(Skier skier, Lesson lesson, Instructor instructor, Date bookingDate) {
        try {
            // check if we can still book it or it' too late
            Period period = Period.getPeriodForDate(lesson.getStartDate());
            if (!isEligibleForBooking(period, bookingDate)) {
                System.out.println("Booking not eligible: date is out of allowed range.");
                return false;
            }

            // check if lesson hasn't already reached max booking
            if (lesson.isLessonFull()) {
                System.out.println("Booking not eligible: lesson has reached max bookings.");
                return false;
            }
            
            if(Booking.isSkierAlreadyBooked(skier, lesson)) {
            	System.out.println("Booking not eligible: this skier already has a booking for this class.");
                return false;
            }
            
            if(!Booking.isAgeValidForLesson(skier, lesson)) {
            	System.out.println("Booking not eligible: this skier is either too young or too old to book this type of lesson.");
                return false;
            }

            Booking booking = new Booking(0, skier, lesson, instructor, period);
            
            boolean success = BookingDAO.insertPrivateBooking(booking);

            if (success) {
                System.out.println("Booking successfully created.");
                return true;
            } else {
                System.out.println("Booking creation failed at DAO level.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
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







}
