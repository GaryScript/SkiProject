package be.alb.models;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.alb.dao.LessonDAO;
import be.alb.dao.SkierDAO;
import be.alb.enums.LessonPeriod;
import be.alb.enums.LessonTypeEnum;
import be.alb.utils.TimeAdjuster;

public class Lesson {
    private int lessonId;
    private Date startDate;
    private Date endDate;
    private Instructor instructor;
    private LessonType lessonType;
    private boolean isPrivate;
    private int minBookings;
    private int maxBookings;
    private boolean isFirstDay;
    private boolean isLastDay;
    private int lessonGroupId;
    
    // Constructor
    public Lesson(int lessonId, Date startDate, Date endDate, Instructor instructor, LessonType lessonType, boolean isPrivate) {
        this.lessonId = lessonId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructor = instructor;
        this.lessonType = lessonType;
        this.isPrivate = isPrivate;

        // set min and max bookings automatically from LessonTypeEnum based on the lessonType name
        try {
            LessonTypeEnum lessonTypeEnum = LessonTypeEnum.fromLessonTypeName(lessonType.getName());
            this.minBookings = lessonTypeEnum.getMinBookings();
            this.maxBookings = lessonTypeEnum.getMaxBookings();
        } catch (IllegalArgumentException e) {
            System.out.println("Error mapping lesson type: " + e.getMessage());
        }
    }
    
    public Lesson(int lessonId, Date startDate, Date endDate, Instructor instructor, 
            LessonType lessonType, boolean isPrivate, boolean isFirstDay, boolean isLastDay, int lessonGroupId) {
		  this.lessonId = lessonId;
		  this.startDate = startDate;
		  this.endDate = endDate;
		  this.instructor = instructor;
		  this.lessonType = lessonType;
		  this.isPrivate = isPrivate;
		  this.isFirstDay = isFirstDay;
		  this.isLastDay = isLastDay;
		  this.lessonGroupId = lessonGroupId;
		
		  try {
		      LessonTypeEnum lessonTypeEnum = LessonTypeEnum.fromLessonTypeName(lessonType.getName());
		      this.minBookings = lessonTypeEnum.getMinBookings();
		      this.maxBookings = lessonTypeEnum.getMaxBookings();
		  } catch (IllegalArgumentException e) {
		      System.out.println("Error mapping lesson type: " + e.getMessage());
		  }
    }
    
    // Getters and Setters
    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public int getMinBookings() {
        return minBookings;
    }

    public int getMaxBookings() {
        return maxBookings;
    }
    
    public boolean isFirstDay() {
        return isFirstDay;
    }

    public void setFirstDay(boolean isFirstDay) {
        this.isFirstDay = isFirstDay;
    }

    public boolean isLastDay() {
        return isLastDay;
    }

    public void setLastDay(boolean isLastDay) {
        this.isLastDay = isLastDay;
    }

    public int getLessonGroupId() {
        return lessonGroupId;
    }

    public void setLessonGroupId(int lessonGroupId) {
        this.lessonGroupId = lessonGroupId;
    }

    public static boolean createLesson(Date startDate, Date endDate, Instructor instructor, LessonType lessonType, boolean isPrivate) {
    	if (isPrivate) {
    	    startDate = TimeAdjuster.adjustTimeToSpecificHour(startDate, LessonPeriod.PRIVATE_HOUR_START);
    	    Lesson privateLesson = new Lesson(0, startDate, endDate, instructor, lessonType, true);
    	    return LessonDAO.createLesson(privateLesson);
    	}
    	else {
            // create lessongroup over 3 days
            List<Lesson> groupLessons = new ArrayList<>();
            long oneDay = 24 * 60 * 60 * 1000; // milliseconds in a day
            Date currentDate = startDate;

            for (int i = 0; i < 3; i++) {
                // Session du matin
                groupLessons.add(new Lesson(
                    0,
                    TimeAdjuster.adjustTimeToPeriod(currentDate, LessonPeriod.MORNING, true), 
                    TimeAdjuster.adjustTimeToPeriod(currentDate, LessonPeriod.MORNING, false), 
                    instructor,
                    lessonType,
                    false
                ));
                // Session de l'après-midi
                groupLessons.add(new Lesson(
                    0,
                    TimeAdjuster.adjustTimeToPeriod(currentDate, LessonPeriod.AFTERNOON, true), 
                    TimeAdjuster.adjustTimeToPeriod(currentDate, LessonPeriod.AFTERNOON, false), 
                    instructor,
                    lessonType,
                    false
                ));

                currentDate = new Date(currentDate.getTime() + oneDay);
            }

            return LessonDAO.createGroupLessons(groupLessons);
        }
    }
    
    public static List<Lesson> getAllLessons() {
        LessonDAO lessonDAO = new LessonDAO(); 
        try {
            return lessonDAO.getAllLessons();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving lessons: " + e.getMessage(), e);
        }
    }
    
    public static List<Lesson> getAllPublicLessons() {
        LessonDAO lessonDAO = new LessonDAO(); 
        try {
            return lessonDAO.getAllPublicLessons();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving lessons: " + e.getMessage(), e);
        }
    }
    
    public static List<Lesson> getAllPrivateLessons() {
        LessonDAO lessonDAO = new LessonDAO(); 
        try {
            return lessonDAO.getAllPrivateLessons();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving lessons: " + e.getMessage(), e);
        }
    }
    
    public boolean isLessonFull() {
    	LessonDAO lessonDAO = new LessonDAO(); 
    	try {
            return lessonDAO.isLessonFull(this);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving lessons: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteLesson() {
    	LessonDAO lessonDAO = new LessonDAO(); 
    	return lessonDAO.deleteLesson(this);
    }
    
    @Override
    public boolean equals(Object obj) {
    	return this.toString()==obj.toString() ;
    }
    	
    @Override
    public int hashCode() {
    	return toString().hashCode();
    }
    
    
    

   
}
