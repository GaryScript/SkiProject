package be.alb.enums;

public enum LessonTypeEnum {
    CHILD(5, 8),
    ADULT(6, 10),
    SNOWBOARD(5, 8),
    COMPETITION(5, 8),
    OFFPISTE(5, 8);

    private final int minBookings;
    private final int maxBookings;

    LessonTypeEnum(int minBookings, int maxBookings) {
        this.minBookings = minBookings;
        this.maxBookings = maxBookings;
    }

    public int getMinBookings() {
        return minBookings;
    }

    public int getMaxBookings() {
        return maxBookings;
    }
}
