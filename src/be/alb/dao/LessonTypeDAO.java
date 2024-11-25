package be.alb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.alb.models.*;
import be.alb.database.*;

public class LessonTypeDAO {
	public List<LessonType> getAllLessonTypes() throws SQLException {
	    List<LessonType> lessonTypes = new ArrayList<>();
	    Connection connection = OracleDBConnection.getInstance();
	    
	    String sql = "SELECT lt.LESSONTYPEID, lt.NAME, lt.AGEGROUP, lt.SPORTTYPE, lt.PRICE, a.ACCREDITATIONID, a.NAME as accreditation_name " +
	                 "FROM LessonType lt " +
	                 "JOIN Accreditation a ON lt.ACCREDITATIONID = a.ACCREDITATIONID";
	    
	    try (PreparedStatement stmt = connection.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            int lessonTypeId = rs.getInt("LESSONTYPEID");
	            String name = rs.getString("NAME");
	            String ageGroup = rs.getString("AGEGROUP");
	            String sportType = rs.getString("SPORTTYPE");
	            double price = rs.getDouble("PRICE");
	            int accreditationId = rs.getInt("ACCREDITATIONID");
	            String accreditationName = rs.getString("accreditation_name");

	            Accreditation accreditation = new Accreditation(accreditationId, accreditationName);

	            LessonType lessonType = new LessonType(lessonTypeId, name, ageGroup, sportType, price, accreditation);
	            lessonTypes.add(lessonType);
	        }
	    }
	    return lessonTypes;
	}

}
