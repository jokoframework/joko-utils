/**
 * 
 */
package io.github.jokoframework.utils.number;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jokoframework.utils.constants.JokoConstants;

/**
 * @author bsandoval
 *
 */
public class NumberUtils {
    
    
    private static final Logger log = LoggerFactory.getLogger(NumberUtils.class);


    /**
     * Retorna un entero positivo entre 1 y el argumento
     *
     * @param maxValue Valor máximo posible para el resultado
     * @return Entero entre 1 y el argumento (Inclusivo)
     */
    public static Integer getRandomInteger(int maxValue) {
        Integer ret = (RandomUtils.nextInt() % maxValue) + 1;
        return ret;
    }

    /**
     * Retorna un String construido al pasar el argumento "numero" a un String y rellenando con 0s a la izquierda o
     * derecha dependiendo del argumento "left" (True izquierda y False derecha) hasta que el número tenga una cantidad
     * de dígitos igual al argumento "zerosQuantity" (De cumplir con esta condición de entrada no se agregaran 0s).
     *
     * @param number Número original
     * @param zerosQuantity Cantidad de dígitos que debe tener el String final (Si es menor a la cantidad de dígitos
     *                      de "number" no se agregaran 0s")
     * @param left Si es True se agregaran los 0s a la izquierda del argumento "numero", si es False a la derecha
     * @return El argumento "numero" en forma de String y con la cantidad de dígitos especificados, rellenando con 0s
     *         a la izquierda o derecha del número original
     */
    public static String padNumberWithZeros(Number number, Integer zerosQuantity, Boolean left) {
        String ret = null;
        if (left)
            ret = StringUtils.leftPad(number.toString(), zerosQuantity, "0");
        else
            ret = StringUtils.rightPad(number.toString(), zerosQuantity, "0");
        return ret;
    }

    /**
     * Retorna un String construido al pasar el argumento "cod" a un String y rellenando con 0s a la izquierda hasta que
     * el número tenga una cantidad de dígitos igual al argumento "zerosQuantity" (De cumplir con esta condición de
     * entrada no se agregaran 0s).
     *
     * @param cod Número original
     * @param zerosQuantity Cantidad de dígitos que debe tener el String final (Si es menor a la cantidad de dígitos
     *                      de "cod" no se agregaran 0s")
     * @return El argumento "cod" en forma de String y con la cantidad de dígitos especificados, rellenando con 0s
     *         a la izquierda del número original
     */
    public static String leftPadZeros(Number cod, Integer zerosQuantity) {
        return padNumberWithZeros(cod, zerosQuantity, true);
    }

    /**
     * Retorna un String construido al pasar el argumento "cod" a un String y rellenando con 0s a la derecha hasta que
     * el número tenga una cantidad de dígitos igual al argumento "zerosQuantity" (De cumplir con esta condición de
     * entrada no se agregaran 0s).
     *
     * @param cod Número original
     * @param zerosQuantity Cantidad de dígitos que debe tener el String final (Si es menor a la cantidad de dígitos
     *                      de "cod" no se agregaran 0s")
     * @return El argumento "cod" en forma de String y con la cantidad de dígitos especificados, rellenando con 0s
     *         a la derecha del número original
     */
    public static String rightPadZeros(Number cod, Integer zerosQuantity) {
        return padNumberWithZeros(cod, zerosQuantity, false);
    }

    /**
     * Formatea el monto pasado
     *
     * @param object String o bigDecimal
     * @return String formateado
     */
    public static String formatAmount(Object object) {
        String ret = "0,00";
        if (object != null && !StringUtils.isBlank(object.toString())) {
            DecimalFormat moneyFormatter = new DecimalFormat(
                    JokoConstants.PATTERN_AMOUNT);
            
            // the locale for Paraguay seems to use "," as thousands separator
            // that's why we don't use it. 
            DecimalFormatSymbols newSymbols = new DecimalFormatSymbols(
                    JokoConstants.DEFAULT_LOCALE);
            BigDecimal valor = new BigDecimal("0");
            newSymbols.setDecimalSeparator(',');
            newSymbols.setGroupingSeparator('.');
            moneyFormatter.setDecimalFormatSymbols(newSymbols);
            moneyFormatter.setParseBigDecimal(true);
            if (object instanceof BigDecimal)
                valor = (BigDecimal) object;
            else
                valor = new BigDecimal(object.toString());
            String formateado = moneyFormatter.format(valor);
            
            // convert 0,0 -> ,00
            String ZEROS = ",00";
            if (!ZEROS.equals(formateado)) {
                if (formateado.endsWith(ZEROS)) {
                    ret = formateado.substring(0,
                            formateado.length() - ZEROS.length());
                } else {
                    int idxComa = formateado.indexOf(",");
                    if (idxComa > 0) {
                        ret = formateado.substring(0, idxComa);
                    } else
                        ret = formateado;
                }
            } else {
                ret = "0";
            }
        } else {
            log.debug("It was requested to format an amount that is empty (empty or null string).");
        }
        return ret;
    }

    /**
     * Formatea la cantidad pasada
     *
     * @param object String o bigDecimal
     * @return formated String
     */
    public static String formatQuantity(Object object) {
        String ret = "0,00";
        if (object != null && !StringUtils.isBlank(object.toString())) {
            DecimalFormat moneyFormatter = new DecimalFormat(
                    JokoConstants.PATTERN_QUANTITY);
            
            // the locale for Paraguay seems to use "," as thousands separator
            // that's why we don't use it. 
            DecimalFormatSymbols newSymbols = new DecimalFormatSymbols(
                    JokoConstants.DEFAULT_LOCALE);
            BigDecimal valor = new BigDecimal("0");
            newSymbols.setDecimalSeparator(',');
            newSymbols.setGroupingSeparator('.');
            moneyFormatter.setDecimalFormatSymbols(newSymbols);
            moneyFormatter.setParseBigDecimal(true);
            if (object instanceof BigDecimal)
                valor = (BigDecimal) object;
            else
                valor = new BigDecimal(object.toString());
            String formateado = moneyFormatter.format(valor);
            
            // convert 0,0 -> ,00
            String ZEROS = ",000";
            if (ZEROS.equals(formateado)) {
                ret = "0";
            } else if (formateado.endsWith(ZEROS))
                ret = formateado.substring(0,
                        formateado.length() - ZEROS.length());
            else
                ret = formateado;
        } else {
            log.debug("It was requested to format a quantity that is empty (empty or null string).");
        }
        return ret;
    }
}
