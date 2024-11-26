package be.alb.utils;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import be.alb.enums.LessonPeriod;

public class TimeAdjuster {

    // Méthode pour ajuster à une heure précise à partir d'une enum LessonPeriod
    public static Date adjustTimeToPeriod(Date currentTime, LessonPeriod period, boolean useStartTime) {
        if (currentTime == null || period == null) {
            throw new IllegalArgumentException("Les paramètres currentTime et period ne doivent pas être nuls !");
        }

        // Convertir java.sql.Date en Instant
        Instant instant = Instant.ofEpochMilli(currentTime.getTime());

        // Convertir l'Instant en LocalDateTime
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Utiliser soit le début soit la fin de la période
        LocalTime targetTime = useStartTime ? period.getStartTime() : period.getEndTime();

        // Ajuster l'heure en fonction de la période
        LocalDateTime adjustedDateTime = localDateTime
            .withHour(targetTime.getHour())
            .withMinute(targetTime.getMinute())
            .withSecond(0)
            .withNano(0);

        // Reconvertir en java.sql.Date
        return new Date(adjustedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
    
    public static Date adjustTimeToSpecificHour(Date currentTime, LocalTime targetTime) {
        if (currentTime == null || targetTime == null) {
            throw new IllegalArgumentException("Les paramètres currentTime et targetTime ne doivent pas être nuls !");
        }

        // Convertir la Date en LocalDateTime
        LocalDateTime localDateTime = Instant.ofEpochMilli(currentTime.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Ajuster l'heure à targetTime
        LocalDateTime adjustedDateTime = localDateTime
                .withHour(targetTime.getHour())
                .withMinute(targetTime.getMinute())
                .withSecond(0)
                .withNano(0);

        // Reconvertir en java.sql.Date
        return new Date(adjustedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

}
