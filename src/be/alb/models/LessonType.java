package be.alb.models;

public class LessonType {
    private int lessonTypeId;
    private String name;
    private String ageGroup;
    private String sportType;
    private double price;
    private Accreditation accreditation; // Référence à l'objet Accreditation

    // Constructeur
    public LessonType(int lessonTypeId, String name, String ageGroup, String sportType, double price, Accreditation accreditation) {
        this.lessonTypeId = lessonTypeId;
        this.name = name;
        this.ageGroup = ageGroup;
        this.sportType = sportType;
        this.price = price;
        this.accreditation = accreditation;
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

    public Accreditation getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(Accreditation accreditation) {
        this.accreditation = accreditation;
    }

    // Méthode pour récupérer l'ID de l'accréditation via l'objet Accreditation
    public int getAccreditationId() {
        return accreditation != null ? accreditation.getAccreditationID() : 0;
    }
}
