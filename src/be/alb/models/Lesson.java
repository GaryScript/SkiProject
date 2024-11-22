	package be.alb.models;
	
	import java.util.Objects;
	
	public class Lesson {
		// variables
	    private LessonType lessonType;  
	    private int minBookings;  
	    private int maxBookings;
	
	   // constructor
	    public Lesson(LessonType lessonType, int minBookings, int maxBookings) {
	        this.lessonType = lessonType;
	        this.minBookings = minBookings;
	        this.maxBookings = maxBookings;
	    }
	
	    // getter and setter
	    public LessonType getLessonType() {
	        return lessonType;
	    }
	
	    public void setLessonType(LessonType lessonType) {
	        this.lessonType = lessonType;
	    }
	
	    public int getMinBookings() {
	        return minBookings;
	    }
	
	    public void setMinBookings(int minBookings) {
	        this.minBookings = minBookings;
	    }
	
	    public int getMaxBookings() {
	        return maxBookings;
	    }
	
	    public void setMaxBookings(int maxBookings) {
	        this.maxBookings = maxBookings;
	    }
	
	//    public double getLessonPrice() {
	//        return lessonType.getPrice(); //  to do actually because there are some promotions
	//    }
	    
	    // override hashcode and equals 
	    
	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj) {
	            return true;
	        }
	        if (obj == null || getClass() != obj.getClass()) {
	            return false;
	        }
	        Lesson other = (Lesson) obj;
	        return lessonType.equals(other.lessonType) && 
	               minBookings == other.minBookings && 
	               maxBookings == other.maxBookings;
	    }
	
	    @Override
	    public int hashCode() {
	        return Objects.hash(lessonType, minBookings, maxBookings);
	    }
	}