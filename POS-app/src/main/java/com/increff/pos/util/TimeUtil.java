package com.increff.pos.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    
    public static String getFormattedTime(ZonedDateTime time, String format) {
        return time.format(DateTimeFormatter.ofPattern(format));
    }

    public static ZonedDateTime getZonedDateTime(String dateTime, DateTimeFormatter format) {
        ZonedDateTime zdt = ZonedDateTime.parse(dateTime, format);
        return zdt;
    }

    public static ZonedDateTime isoTimeStringToZonedDateTime(String isoDateTime) {
        return getZonedDateTime(isoDateTime, DateTimeFormatter.ISO_DATE_TIME);
    }
}
