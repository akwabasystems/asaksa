
package com.akwabasystems.asakusa.utils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * A class that contains utility methods for working with calendars, dates, 
 * time and time zones.
 */
public class Timeline {

    public static final DateTimeFormatter DATE_FORMAT_UTC;
    public static final DateTimeFormatter LOCAL_DATE_FORMAT;
    public static final DateTimeFormatter DATE_FORMAT_YEAR_MONTH_DAY;

    static { 
        DATE_FORMAT_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DATE_FORMAT_YEAR_MONTH_DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
    
   
    /**
     * Formats the specified date in the following UTC format: 2001-01-01T00:00:00Z
     *
     * @param date      the date to format
     * @return a UTC format of the specified date
     */
    public static String toUTCFormat(ZonedDateTime date) {
        return DATE_FORMAT_UTC.format(date);
    }
    
    
    /**
     * Parses a date from the specified UTC string
     *
     * @param dateString        the date string to parse, in UTC format
     * @return the parsed date from the specified string
     */
    public static ZonedDateTime fromUTCFormat(String dateString) {
        ZonedDateTime date;
        
        try {
            
            date = ZonedDateTime.parse(dateString);
            
        } catch(DateTimeParseException cannotParse) {
            date = ZonedDateTime.now(Clock.systemUTC());
        }
        
        return date;
    }
    
    
    /**
     * Returns the current time in UTC milliseconds from the epoch
     * 
     * @return the current time in UTC milliseconds from the epoch
     */
    public static Long currentTimeUTC() {
        return Clock.systemUTC().instant().toEpochMilli();
    }
    
    
    /**
     * Returns the UTC time zone
     * 
     * @return the UTC time zone
     */
    public static ZoneId timezoneUTC() {
        return ZoneId.of("UTC");
    }

     
    /**
     * Returns the UTC representation of the specified local date string
     * 
     * @param localDateString   the date string for which to get the UTC representation
     * @param timezone          the local time zone
     * @return the UTC representation of the specified local date string
     */
    public static String localDateToUTCString(String localDateString, ZoneId timezone) {
        LocalDateTime localMinDate = LocalDateTime.parse(localDateString);
        Instant instant = localMinDate.atZone(timezone).toInstant();
        return instant.atZone(Timeline.timezoneUTC()).format(DATE_FORMAT_UTC);
    }
    
    
    /**
     * Returns the string representation of the current UTC date and time
     * 
     * @return the string representation of the current UTC date and time
     */
    public static String currentDateTimeUTCString() {
        ZonedDateTime dateTime = ZonedDateTime.now(Clock.systemUTC());
        return dateTime.format(DATE_FORMAT_UTC);
    }


    /**
     * Returns a zoned date from the given date components
     * 
     * @param timezone              the time zone in which to convert the date
     * @param components            the date components from which to set the calendar
     * @return a zoned date from the given date components
     */
    public static ZonedDateTime dateFromComponents(ZoneId timezone, String[] components) {
        return (components.length == 0) ? ZonedDateTime.now() : 
               (components.length == 1) ? ZonedDateTime.of(
                Integer.parseInt(components[0]), 1, 1, 
                0, 0, 0, 0, timezone) :
               (components.length == 2) ? ZonedDateTime.of(
                Integer.parseInt(components[0], 10), 
                Integer.parseInt(components[1], 10), 
            1, 0, 0, 0, 0, timezone) : 
                ZonedDateTime.of(
                Integer.parseInt(components[0], 10), 
                Integer.parseInt(components[1], 10), 
            Integer.parseInt(components[2], 10),
                0, 0, 0, 0, timezone);
    }
    
    
    /**
     * Returns a zoned date and time from the given date components
     * 
     * @param timezone              the time zone in which to convert the date and time
     * @param components            the date components from which to set the calendar
     * @return a zoned date and time from the given date components
     */
    public static ZonedDateTime dateTimeFromComponents(ZoneId timezone, String[] components) {
        if (components.length <= 3) {
            return dateFromComponents(timezone, components);
        }
        
        return ZonedDateTime.of(
            Integer.parseInt(components[0], 10), 
            Integer.parseInt(components[1], 10), 
        Integer.parseInt(components[2], 10),
            Integer.parseInt(components[3], 10), 
                (components.length >= 5)? Integer.parseInt(components[4], 10) : 0,
                (components.length >= 6)? Integer.parseInt(components[5], 10) : 0,
                0, 
                timezone
        );
    }
    
}
