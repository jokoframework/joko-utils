/**
 * 
 */
package io.github.jokoframework.utils.csv;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import io.github.jokoframework.utils.exception.JokoUtilsException;

/**
 * @author bsandoval
 *
 */
public class CsvUtils {
    
    private static final Logger log = LoggerFactory.getLogger(CsvUtils.class);

    private CsvUtils () {
        //No public constructor
    }

    /**
     * Representa una lista de objetos en forma de tabla, generando un archivo CSV en base a las columnas especificadas
     *
     * @param list Lista indefinida que contiene objetos de una clase tal que esta sea serializable y tenga los
     *             Getters definidos para las variables privadas que especifica el parametro "columns"
     * @param columns Lista de Strings que especifica los nombres de las cabeceras de columna, para cada celda en una
     *                columna se rellenaran los campos con la variable privada llamada igual que la cabecera de columna
     *                dentro de los objetos de list
     * @param requiredType La clase de los objetos dentro de "list"
     * @return Byte stream de un archivo de text en formato CSV hecho con las columnas y datos proveídos
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static byte[] convertToCsv(List<?> list, List<String> columns, Class requiredType) throws JokoUtilsException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(baos);
            
            //Set columns order
            ColumnPositionMappingStrategy columnStrategy = new ColumnPositionMappingStrategy();
            columnStrategy.setType(requiredType);
            columnStrategy.setColumnMapping(columns.toArray(new String[]{}));
            
            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withMappingStrategy(columnStrategy).build();
            
            //Write headers
            writer.write(StringUtils.join(columns, ',').concat("\n"));
            
            //Write transactions
            beanToCsv.write(list);
            writer.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            throw new JokoUtilsException(e.getMessage(), e);
        }
    }
}
