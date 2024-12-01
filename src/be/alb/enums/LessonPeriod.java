package be.alb.enums;

import java.time.LocalTime;

public enum LessonPeriod {
    MORNING(LocalTime.of(9, 0), LocalTime.of(12, 0)),
    AFTERNOON(LocalTime.of(14, 0), LocalTime.of(17, 0)), 
    PRIVATE_ONE_HOUR(LocalTime.of(12, 0), LocalTime.of(13, 0)), 
    PRIVATE_TWO_HOUR(LocalTime.of(12, 0), LocalTime.of(14, 0));
	
    private final LocalTime startTime;
    private final LocalTime endTime;

    LessonPeriod(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    // Constants for private lessons
    public static final LocalTime PRIVATE_HOUR_START = LocalTime.of(12, 0);
    public static final LocalTime PRIVATE_ONE_HOUR_END = LocalTime.of(13, 0);
    public static final LocalTime PRIVATE_TWO_HOUR_END = LocalTime.of(14, 0);
}
