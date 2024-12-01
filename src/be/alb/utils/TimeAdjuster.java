package be.alb.utils;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import be.alb.enums.LessonPeriod;

public class TimeAdjuster {
    public static Date adjustTimeToPeriod(Date currentTime, LessonPeriod period, boolean useStartTime) {
        if (currentTime == null || period == null) {
            throw new IllegalArgumentException("Les paramètres currentTime et period ne doivent pas être nuls !");
        }

        Instant instant = Instant.ofEpochMilli(currentTime.getTime());

        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalTime targetTime = useStartTime ? period.getStartTime() : period.getEndTime();

        LocalDateTime adjustedDateTime = localDateTime
            .withHour(targetTime.getHour())
            .withMinute(targetTime.getMinute())
            .withSecond(0)
            .withNano(0);

        return new Date(adjustedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
    
    public static Date adjustTimeToSpecificHour(Date currentTime, LocalTime targetTime) {
        if (currentTime == null || targetTime == null) {
            throw new IllegalArgumentException("Les paramètres currentTime et targetTime ne doivent pas être nuls !");
        }

        LocalDateTime localDateTime = Instant.ofEpochMilli(currentTime.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime adjustedDateTime = localDateTime
                .withHour(targetTime.getHour())
                .withMinute(targetTime.getMinute())
                .withSecond(0)
                .withNano(0);
        
        return new Date(adjustedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

}
