package io.github.jokoframework.utils.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Years;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jokoframework.utils.constants.JokoConstants;

/**
 * Utilidades para trabajar con fechas
 * 
 * @author ncanata
 *
 */
public class DateTimeUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeUtils.class);


    public static Date now(){
        return new LocalDate().toDate();
    }
    
    public static Date today() {
        return DateUtils.truncate(now(), Calendar.DATE);
    }
    
    public static int getCurrentMonth(){
        DateTime dateTime = DateTime.now();
        return dateTime.getMonthOfYear();
    }

    public static int getCurrentYear(){
        DateTime dateTime = DateTime.now();
        return dateTime.getYear();
    }

    public static Integer yearsBetween(Date fromDate, Date toDate){
        LocalDate localFromDate = LocalDate.fromDateFields(fromDate);
        LocalDate localToDate = LocalDate.fromDateFields(toDate);
        Years years = Years.yearsBetween(localFromDate, localToDate);
        LOGGER.debug("YEARS: {}", years.getYears());
        return years.getYears();
    }

    public static Integer daysBetween(Date fromDate, Date toDate){
        LocalDate localFromDate = LocalDate.fromDateFields(fromDate);
        LocalDate localToDate = LocalDate.fromDateFields(toDate);
        Days days = Days.daysBetween(localFromDate, localToDate);
        LOGGER.debug("DAYS: {}", days.getDays());
        return days.getDays();
    }

    public static Date firstDayOfMonth(){
        DateTime dateTime = DateTime.now();

        // Get the firts date of the month using the dayOfMonth property
        // and get the maximum value from it.
        DateTime lastDate = dateTime.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        return lastDate.toDate();
    }

    public static Date lastDayOfMonth(){
        DateTime dateTime = DateTime.now();

        // Get the last date of the month using the dayOfMonth property
        // and get the maximum value from it.
        DateTime lastDate = dateTime.dayOfMonth().withMaximumValue();
        return lastDate.toDate();
    }

    public static Integer daysTillEndOfMonth(){
        Integer days = daysBetween(now(), lastDayOfMonth());
        LOGGER.debug("DAYS: {}", days);
        return days;
    }

    public static Date subtractDaysFromDate(Date date, int days){
        DateTime endDate = new DateTime(date);
        DateTime startDate = endDate.minusDays(days);
        return startDate.toDate();
    }

    public static Date subtractMonthFromDate(Date date, int months){
        DateTime endDate = new DateTime(date);
        DateTime startDate = endDate.minusMonths(months);
        return startDate.toDate();
    }

    public static Boolean currentTimeWithin(Integer from, Integer to){
        LocalTime fromTime = LocalTime.fromCalendarFields(calendarFromHour(from.toString()));
        DateTime fromDate =  DateTime.now().withTime(fromTime);
        LocalTime toTime = LocalTime.fromCalendarFields(calendarFromHour(to.toString()));
        DateTime toDate =  DateTime.now().withTime(toTime);
        DateTime currentDate = DateTime.now();
        return fromDate.isBefore(currentDate) && toDate.isAfter(currentDate);
    }

    public static Boolean currentTimeBefore(Integer time){
        LocalTime localTime = LocalTime.fromCalendarFields(calendarFromHour(time.toString()));
        DateTime dateTime =  DateTime.now().withTime(localTime);
        DateTime currentDate = DateTime.now();
        return currentDate.isBefore(dateTime);
    }

    public static Boolean currentTimeAfter(Integer time){
        LocalTime localTime = LocalTime.fromCalendarFields(calendarFromHour(time.toString()));
        DateTime dateTime =  DateTime.now().withTime(localTime);
        DateTime currentDate = DateTime.now();
        return currentDate.isAfter(dateTime);
    }

    public static GregorianCalendar calendarFromHour(final String hh){
        if (hh.matches("^[0-2][0-9]$")){
            final GregorianCalendar gc = new GregorianCalendar();
            gc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hh));
            return gc;
        }else{
            throw new IllegalArgumentException(hh + " is not a valid time, expecting HH format");
        }
    }

    public static Date dateFromHourMinSec(final String hhmmss){
        if (hhmmss.matches("^[0-2][0-9]:[0-5][0-9]:[0-5][0-9]$")){
            final String[] hms = hhmmss.split(":");
            final GregorianCalendar gc = new GregorianCalendar();
            gc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hms[0]));
            gc.set(Calendar.MINUTE, Integer.parseInt(hms[1]));
            gc.set(Calendar.SECOND, Integer.parseInt(hms[2]));
            gc.set(Calendar.MILLISECOND, 0);
            return gc.getTime();
        }else{
            throw new IllegalArgumentException(hhmmss + " is not a valid time, expecting HH:MM:SS format");
        }
    }

    public static Boolean isDay(DayOfWeek day){
        if(day == null){
            return false;
        }
        Locale spanish = new Locale("en", "US");
        DateTime.Property dayOfWeek = DateTime.now().dayOfWeek();
        String stringDay = dayOfWeek.getAsText(spanish);
        return stringDay.toLowerCase().equals(day.toString().toLowerCase());
    }
    
    public static Date shiftDate(Date date, Integer seconds, Integer minutes, Integer hours, Integer days,
            Integer months, Integer years, Boolean delay) {
        return shiftDate(date, 0, seconds, minutes, hours, days, months, years, delay);
    }

    public static Date shiftDate(Date date, Integer miliseconds, Integer seconds, Integer minutes, Integer hours,
            Integer days, Integer months, Integer years, Boolean delay) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        int delayFactor = delay ? -1 : 1;

        if (days != null) {
            c1.add(Calendar.DATE, days.intValue() * delayFactor);
        }
        if (months != null) {
            c1.add(Calendar.MONTH, months.intValue() * delayFactor);
        }
        if (years != null) {
            c1.add(Calendar.YEAR, years.intValue() * delayFactor);
        }

        c1.add(Calendar.SECOND, seconds != null ? (seconds * delayFactor) : 0);
        c1.add(Calendar.MINUTE, minutes != null ? (minutes * delayFactor) : 0);
        c1.add(Calendar.HOUR, hours != null ? (hours * delayFactor) : 0);
        c1.add(Calendar.MILLISECOND, miliseconds != null ? (miliseconds * delayFactor) : 0);

        return new Date(c1.getTimeInMillis());
    }

    public static Date parseDate(String fechaString) {
        Date fecha = null;
        DateFormat parseadorFecha = new SimpleDateFormat(JokoConstants.DATE_TIME_FORMAT);
        try {
            fecha = parseadorFecha.parse(fechaString);
        } catch (ParseException e) {
            fecha = new Date();
            LOGGER.error("Couldn't parse date : " + fechaString, e);
        }
        return fecha;
    }
    
    public static Date getDateUntilFriday() {
        Date now = new Date();
        Date friday = null;
        Calendar cal = Calendar.getInstance();
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        int daysUntilFriday = Calendar.FRIDAY - weekDay;
        Date dateUntilFriday = null;
        if (daysUntilFriday > 0) {
            dateUntilFriday = DateTimeUtils.shiftDate(now, 0, 0, 0, daysUntilFriday, 0, 0, false);
            cal.setTime(dateUntilFriday);
        }
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 00);
        friday = cal.getTime();
        return friday;
    }

    public static Date getDateUntilEndOfMonth() {
        Date now = new Date();
        Date endOfMonth = null;
        Calendar cal = Calendar.getInstance();
        int monthDay = cal.get(Calendar.DAY_OF_MONTH);
        int daysUntilEndOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                - monthDay;
        Date dateUntilEndOfMonth = null;
        if (daysUntilEndOfMonth > 0) {
            dateUntilEndOfMonth = DateTimeUtils.shiftDate(now, 0, 0, 0,
                    daysUntilEndOfMonth, 0, 0, false);
            cal.setTime(dateUntilEndOfMonth);
        }
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 00);
        endOfMonth = cal.getTime();
        return endOfMonth;
    }

    public static Date getDateUntilEndOfYear() {
        Date now = new Date();
        Date endOfYear = null;
        Calendar cal = Calendar.getInstance();
        int yearDay = cal.get(Calendar.DAY_OF_YEAR);
        int daysUntilEndOfYear = cal.getActualMaximum(Calendar.DAY_OF_YEAR)
                - yearDay;
        Date dateUntilEndOfYear = null;
        if (daysUntilEndOfYear > 0) {
            dateUntilEndOfYear = DateTimeUtils.shiftDate(now, 0, 0, 0,
                    daysUntilEndOfYear, 0, 0, false);
            cal.setTime(dateUntilEndOfYear);
            cal.set(Calendar.HOUR_OF_DAY, 17);
            cal.set(Calendar.MINUTE, 00);
            endOfYear = cal.getTime();
        }
        return endOfYear;
    }

}
