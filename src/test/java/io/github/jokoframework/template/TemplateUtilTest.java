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
}
