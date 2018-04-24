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

    /**
     * Retorna la fecha y tiempo actual.
     *
     * @return Fecha y tiempo actual
     */
    public static Date now(){
        return new LocalDate().toDate();
    }

    /**
     * Retorna la fecha del día actual con campos de tiempo en 0 (H:M:S 00:00:00).
     *
     * @return Fecha del día actual con campos de tiempo en 0 (H:M:S 00:00:00)
     */
    public static Date today() {
        return DateUtils.truncate(now(), Calendar.DATE);
    }

    /**
     * Retorna el número del mes actual (1 a 12).
     *
     * @return Número del mes actual (1 a 12)
     */
    public static int getCurrentMonth(){
        DateTime dateTime = DateTime.now();
        return dateTime.getMonthOfYear();
    }

    /**
     * Retorna el número del año actual.
     *
     * @return Número del año actual
     */
    public static int getCurrentYear(){
        DateTime dateTime = DateTime.now();
        return dateTime.getYear();
    }

    /**
     * Retorna un objeto con la cantidad de años entre las dos fechas proveídas.
     * <p>
     * El valor retornado es el resultado de la resta "toDate - fromDate" (No saltan
     * errores al retornar valores negativos).
     *
     * @param fromDate Fecha inferior
     * @param toDate Fecha superior
     * @return Cantidad de años entre las dos fechas proveídas
     */
    public static Integer yearsBetween(Date fromDate, Date toDate){
        LocalDate localFromDate = LocalDate.fromDateFields(fromDate);
        LocalDate localToDate = LocalDate.fromDateFields(toDate);
        Years years = Years.yearsBetween(localFromDate, localToDate);
        LOGGER.debug("YEARS: {}", years.getYears());
        return years.getYears();
    }

    /**
     * Retorna un objeto con la cantidad de dias entre las dos fechas proveídas.
     * <p>
     * El valor retornado es el resultado de la resta "toDate - fromDate" (No saltan
     * errores al retornar valores negativos).
     *
     * @param fromDate Fecha inferior
     * @param toDate Fecha superior
     * @return Cantidad de años entre las dos fechas proveídas
     */
    public static Integer daysBetween(Date fromDate, Date toDate){
        LocalDate localFromDate = LocalDate.fromDateFields(fromDate);
        LocalDate localToDate = LocalDate.fromDateFields(toDate);
        Days days = Days.daysBetween(localFromDate, localToDate);
        LOGGER.debug("DAYS: {}", days.getDays());
        return days.getDays();
    }

    /**
     * Retorna la fecha del primer día del mes actual con campos de tiempo en 0 (H:M:S 00:00:00).
     *
     * @return Fecha del primer día del mes actual con campos de tiempo en 0 (H:M:S 00:00:00)
     */
    public static Date firstDayOfMonth(){
        DateTime dateTime = DateTime.now();

        // Get the firts date of the month using the dayOfMonth property
        // and get the maximum value from it.
        DateTime lastDate = dateTime.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        return lastDate.toDate();
    }

    /**
     * Retorna la fecha del último día del mes actual con campos de tiempo en 0 (H:M:S 00:00:00).
     *
     * @return Fecha del último día del mes actual con campos de tiempo en 0 (H:M:S 00:00:00)
     */
    public static Date lastDayOfMonth(){
        DateTime dateTime = DateTime.now();

        // Get the last date of the month using the dayOfMonth property
        // and get the maximum value from it.
        DateTime lastDate = dateTime.dayOfMonth().withMaximumValue();
        return lastDate.toDate();
    }

    /**
     * Retorna un objeto con los días que faltan para el final del mes actual.
     *
     * @return Días que faltan para el final del mes actual
     */
    public static Integer daysTillEndOfMonth(){
        Integer days = daysBetween(now(), lastDayOfMonth());
        LOGGER.debug("DAYS: {}", days);
        return days;
    }

    /**
     * Retorna la fecha resultante de restar la cantidad de días proveídos a la fecha proveída,
     * el tiempo de la fecha resultante mantiene el valor de la fecha proveída.
     *
     * @param date Fecha y tiempo a la que se le restaran los días
     * @param days Cantidad de días que se quieren restar a la fecha
     * @return Fecha resultante de restar la cantidad de días proveídos a la fecha proveída,
     * el tiempo de la fecha física resultante mantiene el valor de la fecha proveída.
     */
    public static Date subtractDaysFromDate(Date date, int days){
        DateTime endDate = new DateTime(date);
        DateTime startDate = endDate.minusDays(days);
        return startDate.toDate();
    }

    /**
     * Retorna la fecha resultante de restar la cantidad de meses proveídos a la fecha proveída,
     * el tiempo de la fecha resultante mantiene el valor de la fecha proveída.
     *
     * @param date Fecha y tiempo a la que se le restaran los meses
     * @param months Cantidad de meses que se quieren restar a la fecha
     * @return Fecha resultante de restar la cantidad de meses proveídos a la fecha proveída,
     * el tiempo de la fecha física resultante mantiene el valor de la fecha proveída.
     */
    public static Date subtractMonthFromDate(Date date, int months){
        DateTime endDate = new DateTime(date);
        DateTime startDate = endDate.minusMonths(months);
        return startDate.toDate();
    }

    /**
     * Retorna True si es que la tiempo actual se encuentra entre las horas proveídas, sino retorna False.
     *
     * @param from Limite inferior (Hora del dia en formato de numero)
     * @param to Limite superior (Hora del dia en formato de numero)
     * @return True si es que el tiempo actual se encuentra entre las horas proveídas, sino False
     */
    public static Boolean currentTimeWithin(Integer from, Integer to){
        LocalTime fromTime = LocalTime.fromCalendarFields(calendarFromHour(from.toString()));
        DateTime fromDate =  DateTime.now().withTime(fromTime);
        LocalTime toTime = LocalTime.fromCalendarFields(calendarFromHour(to.toString()));
        DateTime toDate =  DateTime.now().withTime(toTime);
        DateTime currentDate = DateTime.now();
        return fromDate.isBefore(currentDate) && toDate.isAfter(currentDate);
    }

    /**
     * Retorna True si es que el tiempo actual se encuentra antes que la hora proveída, sino retorna False.
     *
     * @param time Limite superior (Hora del dia en formato de numero)
     * @return True si es que el tiempo actual se encuentra antes que la hora proveída, sino False
     */
    public static Boolean currentTimeBefore(Integer time){
        LocalTime localTime = LocalTime.fromCalendarFields(calendarFromHour(time.toString()));
        DateTime dateTime =  DateTime.now().withTime(localTime);
        DateTime currentDate = DateTime.now();
        return currentDate.isBefore(dateTime);
    }

    /**
     * Retorna True si es que el tiempo actual se encuentra despues que la hora proveída, sino retorna False.
     *
     * @param time Limite inferior (Hora del dia en formato de numero)
     * @return True si es que el tiempo actual se encuentra despues que la hora proveída, sino False
     */
    public static Boolean currentTimeAfter(Integer time){
        LocalTime localTime = LocalTime.fromCalendarFields(calendarFromHour(time.toString()));
        DateTime dateTime =  DateTime.now().withTime(localTime);
        DateTime currentDate = DateTime.now();
        return currentDate.isAfter(dateTime);
    }

    /**
     * Retorna un GregorianCalendar al cual se le asigna la hora proveída con el formato "HH" (Valor entre "00" y "29").
     *
     * @param hh String con horas con las que se inicializa el calendario, formato "HH" (Valor entre "00" y "29")
     * @return GregorianCalendar con la hora proveída asignada
     */
    public static GregorianCalendar calendarFromHour(final String hh){
        if (hh.matches("^[0-2][0-9]$")){
            final GregorianCalendar gc = new GregorianCalendar();
            gc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hh));
            return gc;
        }else{
            throw new IllegalArgumentException(hh + " is not a valid time, expecting HH format");
        }
    }

    /**
     * Retorna la fecha actual con el tiempo proveído en el formato "hh:mm:ss" donde la hora esta entre "00" y "29" y
     * los minutos y segundos entre "00" y "59" (De poner más de 23 horas en el campo de hora se ajustara el día de la
     * fecha).
     *
     * @param hhmmss String con horas, minutos y segundos con los que se inicializa el tipo Date, formato "hh:mm:ss"
     *               (Valor entre "00" y "29" para hh y entre "00" y "59" para mm y ss)
     * @return La fecha actual con el tiempo proveído
     */
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

    /**
     * Retorna True si el día de la semana actual es igual al proveído, sino False.
     *
     * @param day Dia de la semana del tipo {@link java.time.DayOfWeek}
     * @return Retorna True si el dia de la semana actual es el dia de la semana proveído
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/time/DayOfWeek.html">DayOfWeek</a>
     */
    public static Boolean isDay(DayOfWeek day){
        if(day == null){
            return false;
        }
        Locale spanish = new Locale("en", "US");
        DateTime.Property dayOfWeek = DateTime.now().dayOfWeek();
        String stringDay = dayOfWeek.getAsText(spanish);
        return stringDay.toLowerCase().equals(day.toString().toLowerCase());
    }

    /**
     * Retorna la fecha y tiempo pasada sumándole (Si delay es False) o restándole (Si delay es True) los demás campos
     * proveídos con sus respectivos campos.
     * <p>
     * Los campos que se pueden sumar/restar son los de Segundos, Minutos, Horas, Días, Meses y Años.
     *
     * @param date Fecha a la que se le sumaran o restaran los demas campos
     * @param seconds Segundos a sumar o restar
     * @param minutes Minutos a sumar o restar
     * @param hours Horas a sumar o restar
     * @param days Días a sumar o restar
     * @param months Meses a sumar o restar
     * @param years Años a sumar o restar
     * @param delay Si es False se suman los campos a date si es True se restan
     * @return La suma o resta (Depende del valor de delay) del campo fecha y tiempo "date" con los demás campos
     * respectivos
     */
    public static Date shiftDate(Date date, Integer seconds, Integer minutes, Integer hours, Integer days,
            Integer months, Integer years, Boolean delay) {
        return shiftDate(date, 0, seconds, minutes, hours, days, months, years, delay);
    }

    /**
     * Retorna la fecha y tiempo pasada sumándole (Si delay es False) o restándole (Si delay es True) los demás campos
     * proveídos con sus respectivos campos.
     * <p>
     * Los campos que se pueden sumar/restar son los de Milisegundos, Segundos, Minutos, Horas, Días, Meses y Años.
     *
     * @param date Fecha a la que se le sumaran o restaran los demas campos
     * @param miliseconds Milisegundos a sumar o restar
     * @param seconds Segundos a sumar o restar
     * @param minutes Minutos a sumar o restar
     * @param hours Horas a sumar o restar
     * @param days Días a sumar o restar
     * @param months Meses a sumar o restar
     * @param years Años a sumar o restar
     * @param delay Si es False se suman los campos a date si es True se restan
     * @return La suma o resta (Depende del valor de delay) del campo fecha y tiempo "date" con los demas campos
     * respectivos
     */
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

    /**
     * Se analiza el string proveído para retornar un objeto del tipo Date con los datos obtenidos del string, el
     * formato definido que debe tener el string es: "dd/MM/yyyy HH:mm:ss" (Día, Mes, Año, Hora, Minuto, Segundo).
     *
     * @param fechaString String con el formato "dd/MM/yyyy HH:mm:ss" (Día, Mes, Año, Hora, Minuto, Segundo) con los
     *                    que se creara el objeto Date
     * @return Objeto Date construido en base al string proveído
     */
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

    /**
     * Retorna la fecha y tiempo hasta el viernes siguiente.
     *
     * @return Fecha y tiempo hasta el viernes siguiente
     */
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

    /**
     * Retorna la fecha y tiempo hasta el fin de mes.
     *
     * @return Fecha y tiempo hasta el fin de mes
     */
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

    /**
     * Retorna la fecha y tiempo hasta el fin de año.
     *
     * @return Fecha y tiempo hasta el fin de año
     */
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
