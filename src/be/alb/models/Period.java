package be.alb.models;

import java.time.LocalDate;

public class Period {

    private int periodId; 
    private LocalDate startDate; 
    private LocalDate endDate;   
    private boolean isVacation; 

    public Period(LocalDate startDate, LocalDate endDate, boolean isVacation) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isVacation = isVacation;
    }

    public Period(int periodId, LocalDate startDate, LocalDate endDate, boolean isVacation) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isVacation() {
        return isVacation;
    }

    public void setVacation(boolean vacation) {
        isVacation = vacation;
    }
}

