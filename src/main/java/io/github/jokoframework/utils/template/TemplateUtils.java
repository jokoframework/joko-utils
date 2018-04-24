package io.github.jokoframework.utils.template;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase utilitaria para tomar un mensaje que posee valores entre llaves y
 * los reemplaza por el valor proporcionado en el mapa.
 * Created by danicricco on 4/24/18.
 */
public class TemplateUtils {

    //expresion regular para valores dentro de curly braces
    private static final Pattern INTERPOLATION_PATTERN = Pattern.compile("\\{(\\w+)(.*?)\\}");

    /**
     * Formatea un template con tokens (encerrados entre llaves) a partir de
     * los valores
     * en un diccionario
     *
     * @param template  texto a ser formateado
     * @param valuesMap Diccionario con valores a ser reemplazados en el template
     * @return {@link String}
     */
    public static String formatMap(String template, Map<String, Object> valuesMap) {
        StringBuilder formatter = new StringBuilder(template);
        List<Object> valueList = new ArrayList<>();

        Matcher matcher = INTERPOLATION_PATTERN.matcher(template);

        while (matcher.find()) {
            String key = matcher.group(1);
            String rest = matcher.group(2);

            String formatKey = String.format("{%s%s}", key, rest);
            int index = formatter.indexOf(formatKey);

            if (index != -1) {
                Object value = null;
                if (valuesMap != null) {
                    value = valuesMap.get(key);
                }
                if (value != null) {
                    String formatValue = String.format("{%d%s}", valueList.size(), rest);
                    formatter.replace(index, index + formatKey.length(), formatValue);
                    valueList.add(value);
                } else {
                    throw new ArrayIndexOutOfBoundsException(String.format("Pattern key %s not found in dictionary", key));
                }
            }
        }

        return MessageFormat.format(formatter.toString(), valueList.toArray());
    }

}
