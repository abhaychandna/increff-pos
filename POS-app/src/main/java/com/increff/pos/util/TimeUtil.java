package com.increff.pos.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;

public class TimeUtil {
    
    @Value("${server.timezone}")
    private static String serverTimezone;

    public static String getFormattedTime(ZonedDateTime time, String format) {
        return time.format(new DateTimeFormatterBuilder().appendPattern(format).toFormatter());
    }

    public static ZonedDateTime getZonedDateTime(String dateTime, DateTimeFormatter format) throws ApiException {
        try {
            return ZonedDateTime.parse(dateTime, format);
        }
        catch (Exception e) {
            throw new ApiException("Failed to parse date time: " + dateTime);
        }
    }

	public static ZonedDateTime getCurrentZonedDateSetTimeZero(){
		return ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

    public static ZonedDateTime isoTimeStringToZonedDateTime(String isoDateTime) throws ApiException {
        return getZonedDateTime(isoDateTime, DateTimeFormatter.ISO_DATE_TIME);
    }

    public static void setDefaultServerTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(serverTimezone));
    }
}
