package com.devfactory.codefix.test.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.TimeZone;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

    public static Instant createInstant(int year, int month, int day, int hours, int minutes, int seconds) {
        return new Calendar.Builder()
                .setDate(year, month, day)
                .setTimeOfDay(hours, minutes, seconds)
                .setTimeZone(TimeZone.getTimeZone("UTC"))
                .build()
                .toInstant();
    }

    public static LocalDateTime createLocalDateTime(int year, int month, int day, int hours, int minutes, int seconds) {
        return LocalDateTime.ofInstant(createInstant(year, month, day, hours, minutes, seconds), ZoneOffset.UTC);
    }
}
