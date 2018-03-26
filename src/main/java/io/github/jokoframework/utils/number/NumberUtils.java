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
     * Returns a positive integer between 1 and the maxValue
     * 
     * @param maxValue max wanted value for the number
     * @return random integer
     */
    public static Integer getRandomInteger(int maxValue) {
        Integer ret = (RandomUtils.nextInt() % maxValue) + 1;
        return ret;
    }

    public static String padNumberWithZeros(Number number, Integer zerosQuantity, Boolean left) {
        String ret = null;
        if (left)
            ret = StringUtils.leftPad(number.toString(), zerosQuantity, "0");
        else
            ret = StringUtils.rightPad(number.toString(), zerosQuantity, "0");
        return ret;
    }

    public static String leftPadZeros(Number cod, Integer zerosQuantity) {
        return padNumberWithZeros(cod, zerosQuantity, true);
    }

    public static String rightPadZeros(Number cod, Integer zerosQuantity) {
        return padNumberWithZeros(cod, zerosQuantity, false);
    }
    
    /**
     * Formats the given amount
     * @param object string or bigDecimal 
     * @return formated String
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
     * Formats the given quantity
     * @param object string or bigDecimal
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
