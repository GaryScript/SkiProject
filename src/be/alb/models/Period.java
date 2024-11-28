package be.alb.models;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

import be.alb.dao.PeriodDAO;

public class Period {

    private int periodId; 
    private Date startDate; 
    private Date endDate;   
    private boolean isVacation; 

    public Period(Date startDate, Date endDate, boolean isVacation) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isVacation = isVacation;
    }

    public Period(int periodId, Date startDate, Date endDate, boolean isVacation) {
        this.periodId = periodId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isVacation = isVacation;
    }

    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isVacation() {
        return isVacation;
    }

    public void setVacation(boolean vacation) {
        isVacation = vacation;
    }
    
    public static Period getPeriodForDate(Date date) throws SQLException {
        return PeriodDAO.getPeriodForDate(date);
    }    
}

