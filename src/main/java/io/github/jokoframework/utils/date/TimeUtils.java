package io.github.jokoframework.utils.date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utilitarios de manejo del tiempo
 *
 */
public class TimeUtils {

    private TimeUtils() {
        super();
    }

    /**
     * Obtener la hora local
     *
     * @return calendar
     */
    public static Calendar getLocalCurrentTime() {
        return Calendar.getInstance(TimeZone.getDefault());
    }

    /**
     * Formatea la fecha de acuerdo a un patron dado
     *
     * @param date fecha
     * @return fecha formateada
     */
    public static String formatDate(Date date, String dateFormat) {
        if (date != null) {
            return DateFormatUtils.formatUTC(date, dateFormat);
        }
        else {
            return null;
        }
    }

    /**
     * Formatea la fecha de acuerdo a un patron dado
     *
     * @param dateString fecha
     * @return fecha formateada
     */
    public static Date formatDateTimeString(String dateString, String dateTimeSecondsFormat) {
        try {
            return DateUtils.parseDate(dateString, dateTimeSecondsFormat);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Formatea la hora de acuerdo a un patron dado
     *
     * @param date fecha
     * @return hora formateada
     */
    public static String formatTime(Date date, String timeFormat) {
        if (date != null) {
            return DateFormatUtils.formatUTC(date, timeFormat);
        }
        else {
            return null;
        }
    }

    /**
     * Crea una fecha en base a una cadena
     *
     * @param dateString fecha formateada
     * @return fecha
     */
    public static Date formatDateString(String dateString, String dateFormat) {
        try {
            return DateUtils.parseDate(dateString, dateFormat);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Obtiene el dia local actual
     *
     * @return dia
     */
    public static Date today() {
        return DateUtils.truncate(getLocalCurrentTime().getTime(), Calendar.DATE);
    }

}
