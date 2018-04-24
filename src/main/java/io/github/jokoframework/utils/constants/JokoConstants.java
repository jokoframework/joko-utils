/**
 * 
 */
package io.github.jokoframework.utils.constants;

import java.util.Locale;

/**
 * Clase que contiene constantes utilizadas por las distintas clases, algunas de estas constantes deberían de
 * removerse y dar la opcion de meter como input a la hora de usar las clases
 *
 * @author bsandoval
 */
public class JokoConstants {

    public static final String PATTERN_AMOUNT = "#,###.00";
    public static final Locale DEFAULT_LOCALE = new Locale("es", "PY");
    public static final String[] MIME_TYPE_PDF = { "application/pdf" };
    public static final String PARAGUAY = "PY";
    public static final String LOCAL_CURRENCY = "Gs";
    
    public static final String PATTERN_QUANTITY = "#,###.##############";
    
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    
    public static final String LATIN1_CHARSET = "ISO-8859-1";
}
