package io.github.jokoframework.template;

import io.github.jokoframework.utils.template.TemplateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by danicricco on 4/24/18.
 */
public class TemplateUtilTest {

    @Test
    public void testStr() {

        String name = "Daniel";
        Integer puntos = 400;

        String template = "Hola {name} tu tienes {puntos} puntos acumulados";
        String expectedValue = "Hola "+name+" tu tienes "+puntos+" puntos acumulados";

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", name);
        values.put("puntos", puntos);
        String value = TemplateUtils.formatMap(template, values);
        Assert.assertEquals(expectedValue, value);

    }

    @Test
    public void testEscapeCurlyBraces() {

        String pin = "320019";

        String template = "Vea su pin entre llaves al final del mensaje. '{'{pin}'}'";
        String expectedValue = "Vea su pin entre llaves al final del mensaje. {" + pin + "}";

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("pin", pin);
        String value = TemplateUtils.formatMap(template, values);
        Assert.assertEquals(expectedValue, value);

    }

    @Test
    public void testFormatType() {

        Integer n = 21;

        String template = "Nació hace {n,number} años";
        String expectedValue = "Nació hace " + n + " años";

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("n", n);
        String value = TemplateUtils.formatMap(template, values);
        Assert.assertEquals(expectedValue, value);

    }
}
