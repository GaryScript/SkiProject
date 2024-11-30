package be.alb.models;

import be.alb.dao.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Accreditation {
	
	// variables 
	
    private int accreditationID;
    private String name;

    // constructor 
    
    public Accreditation(int accreditationID, String name) {
        this.accreditationID = accreditationID;
        this.name = name;
    }

    // Getters et Setters
    public int getAccreditationID() {
        return accreditationID;
    }

    public void setAccreditationID(int accreditationID) {
        this.accreditationID = accreditationID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    // call the dao to get all DAO 
    public static List<Accreditation> getAllAccreditations() {
        return AccreditationDAO.getAllAccreditations();
    }
    
    public static boolean addAccreditationsToInstructor(Instructor instructor)
    {
    	AccreditationDAO accreditationDAO = new AccreditationDAO();
    	return accreditationDAO.addAccreditationsToInstructor(instructor);
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
