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
