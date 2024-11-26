package be.alb.utils;

import java.time.LocalTime;

public class TimeAdjuster {

    public static LocalTime adjustTime(LocalTime currentTime, int adjustHour) {
    	
        if (adjustHour < 0 || adjustHour > 23) {
            throw new IllegalArgumentException("Heure d'ajustement invalide !");
        }
        
        return currentTime.withHour(adjustHour).withMinute(0).withSecond(0);
    }
}
