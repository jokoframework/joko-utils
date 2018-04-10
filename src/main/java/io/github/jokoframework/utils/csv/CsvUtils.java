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

    /**
     * Recibe una lista de Objetos de la misma clase, una lista con el nombre de cada atributo del objeto a aparecer en
     * el texto CSV y la clase de los Objetos de la primera lista, se retorna el byte stream de un archivo de texto en
     * formato CSV con los datos especificado.
     * <p>
     * La clase de los objetos pasados debe ser serializable y los elementos de la segunda lista deben ser atributos de
     * los objetos que contengan Getters (Sino la columna quedara con todas sus entradas en blanco).
     *
     * @param list Lista indefinida que contiene objetos de una clase tal que esta clase sea serializable y tenga los
     *             Getters definidos para las variables privadas que especifica el parametro "columns"
     * @param columns Lista de Strings que especifica que variables privadas se usaran de cada objeto de "list" al
     *                hacer el texto CSV
     * @param requiredType La clase de los objetos dentro de "list"
     * @return Byte stream de un texto en formato CSV hecho con las columnas y datos proveídos
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
            log.error(e.getMessage(), e);
            throw new JokoUtilsException(e.getMessage(), e);
        }
    }
}
