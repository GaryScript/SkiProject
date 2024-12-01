package be.alb.dao;

import be.alb.models.Period;
import java.sql.Date;
import java.sql.SQLException;

public interface PeriodDAOInterface {
    Period getPeriodForDate(Date date) throws SQLException;
}
