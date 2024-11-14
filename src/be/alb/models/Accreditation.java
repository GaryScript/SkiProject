package be.alb.models;

import be.alb.dao.*;
import java.util.List;
import java.util.Objects;

import be.alb.dao.InstructorDAO;

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

    // override equals and hashcode so it works when comparing value and not ref
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accreditation that = (Accreditation) o;
        return accreditationID == that.accreditationID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accreditationID); 
    }
}
