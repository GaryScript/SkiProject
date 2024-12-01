package be.alb.dao;

import be.alb.models.Accreditation;
import be.alb.models.Instructor;

import java.sql.SQLException;
import java.util.List;

public interface AccreditationDAOInterface {
    List<Accreditation> getAllAccreditations();
    boolean addAccreditationsToInstructor(Instructor instructor) throws SQLException;
}
