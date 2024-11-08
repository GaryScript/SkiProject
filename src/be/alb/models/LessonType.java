package be.alb.models;

public class LessonType {
	private String level; // not sure it's a string
	private double price;
	
	public LessonType(String level, double price) {
        this.level = level;
        this.price = price;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
