package be.alb.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.alb.database.OracleDBConnection;
import be.alb.models.Period;

public class PeriodDAO {
	
	public static Period getPeriodForDate(Date date) throws SQLException {
	    Period period = null;
	    String query = "SELECT * FROM Periods WHERE ? BETWEEN startDate AND endDate";
	    try (PreparedStatement stmt = OracleDBConnection.getInstance().prepareStatement(query)) {
	        stmt.setDate(1, date);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            boolean isVacation = rs.getInt("isVacation") == 1;
	            period = new Period(rs.getInt("periodId"), rs.getDate("startDate"), rs.getDate("endDate"), isVacation);
	        }
	    }
	    return period;
	}


}
