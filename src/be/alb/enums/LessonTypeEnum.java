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

    public static LessonTypeEnum fromLessonTypeName(String lessonTypeName) {
        switch (lessonTypeName) {
            case "Télémark 1-4":
            case "Ski Adulte Niveau 1-4":
            case "Ski Adulte Hors-piste":
                return ADULT;
            case "Ski de Fond 1-4":
            case "Ski Bronze":
            case "Ski Argent":
            case "Ski Or":
            case "Ski Platine":
            case "Ski Diamant":
            case "Ski Compétition":
            case "Ski Hors-piste":
            case "Snowboard Niveau 1":
            case "Snowboard Niveau 2-4":
            case "Snowboard Hors-piste":
                return CHILD;
            default:
                throw new IllegalArgumentException("Unknown lesson type: " + lessonTypeName);
        }
    }
}
