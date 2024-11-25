package be.alb.models;

public class LessonType {
    private int lessonTypeId;
    private String name;
    private String ageGroup;
    private String sportType;
    private double price;
    private int accreditationId;

    public LessonType(int lessonTypeId, String name, String ageGroup, String sportType, double price, int accreditationId) {
        this.lessonTypeId = lessonTypeId;
        this.name = name;
        this.ageGroup = ageGroup;
        this.sportType = sportType;
        this.price = price;
        this.accreditationId = accreditationId;
    }

    // Getters et Setters
    public int getLessonTypeId() {
        return lessonTypeId;
    }

    public void setLessonTypeId(int lessonTypeId) {
        this.lessonTypeId = lessonTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAccreditationId() {
        return accreditationId;
    }

    public void setAccreditationId(int accreditationId) {
        this.accreditationId = accreditationId;
    }
}
