package be.alb.models;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import be.alb.dao.LessonDAO;
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

    // Constructor
    public Lesson(int lessonId, Date startDate, Date endDate, Instructor instructor, LessonType lessonType, boolean isPrivate) {
        this.lessonId = lessonId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructor = instructor;
        this.lessonType = lessonType;
        this.isPrivate = isPrivate;

        // Set min and max bookings automatically from LessonTypeEnum based on the lessonType name
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

    // Static method to handle lesson creation
    public static boolean createLesson(Date startDate, Date endDate, Instructor instructor, LessonType lessonType, boolean isPrivate) {
    	if (isPrivate) {
    	    startDate = TimeAdjuster.adjustTimeToSpecificHour(startDate, LessonPeriod.PRIVATE_HOUR_START);
    	    Lesson privateLesson = new Lesson(0, startDate, endDate, instructor, lessonType, true);
    	    return LessonDAO.createLesson(privateLesson);
    	}
    	else {
            // Créer des cours collectifs sur 3 jours
            List<Lesson> groupLessons = new ArrayList<>();
            long oneDay = 24 * 60 * 60 * 1000; // Millisecondes dans un jour
            Date currentDate = startDate;

            for (int i = 0; i < 3; i++) {
                // Session du matin
                groupLessons.add(new Lesson(
                    0,
                    TimeAdjuster.adjustTimeToPeriod(currentDate, LessonPeriod.MORNING, true), 
                    TimeAdjuster.adjustTimeToPeriod(currentDate, LessonPeriod.MORNING, false), // Fin matinée (12h)
                    instructor,
                    lessonType,
                    false
                ));
                // Session de l'après-midi
                groupLessons.add(new Lesson(
                    0,
                    TimeAdjuster.adjustTimeToPeriod(currentDate, LessonPeriod.AFTERNOON, true), // Début après-midi (13h)
                    TimeAdjuster.adjustTimeToPeriod(currentDate, LessonPeriod.AFTERNOON, false), // Fin après-midi (17h)
                    instructor,
                    lessonType,
                    false
                ));

                // Passer au jour suivant
                currentDate = new Date(currentDate.getTime() + oneDay);
            }

            // Sauvegarder tous les cours collectifs
            return LessonDAO.createGroupLessons(groupLessons);
        }
    }
}
