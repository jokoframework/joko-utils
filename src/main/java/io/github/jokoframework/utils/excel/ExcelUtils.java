package io.github.jokoframework.utils.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Table;

import io.github.jokoframework.utils.exception.JokoUtilsException;

/**
 * Excel Generator
 */
public class ExcelUtils {

    /**
     * Recibe una tabla con datos, una lista los nombres de las columnas de la tabla (Nombres en orden de aparición) y
     * la dirección mas nombre del archivo que se creara (/home/user/mi_excel.xls), al finalizar el método se
     * habrá creado un archivo Excel con los datos, dirección y nombre especificados.
     *
     * @param data     Tabla con los datos a incluir en el archivo Excel final
     * @param header   Lista con los nombres de cada columna (En orden de aparición)
     * @param fileName Nombre del archivo Excel final, incluyendo el camino
     * @throws JokoUtilsException Excepción de Joko Utils
     */
    public void generateExcel(Table<Integer, Integer, Object> data,
                              List<String> header, String fileName) throws JokoUtilsException {
        generateReportFile(data, header, fileName);
    }

    /**
     * Recibe una tabla con datos, una lista los nombres de las columnas de la tabla (Nombres en orden de aparición) y
     * la dirección mas nombre del archivo que se creara (/home/user/mi_excel.xls), al finalizar el método se
     * habrá creado un archivo Excel con los datos, dirección y nombre especificados.
     *
     * @param data     Tabla con los datos a incluir en el archivo Excel final
     * @param header   Lista con los nombres de cada columna (En orden de aparición)
     * @param fileName Nombre del archivo Excel final, incluyendo el camino
     * @throws JokoUtilsException Excepción de Joko Utils
     */
    public void generateReportFile(Table<Integer, Integer, Object> data, List<String> header, String fileName)
            throws JokoUtilsException {

        // 1. Create a new Workbook
        try (Workbook wb = new XSSFWorkbook()) {
            int rowCount = 0;
            // 2. Create a new sheet
            Sheet sheet = wb.createSheet("sheet 1");
            Object registro = null;
            if (!data.isEmpty()) {
                Row row = sheet.createRow(rowCount);
                for (int i = 0; i < header.size(); i++) {
                    // 3. Create a header row
                    row.createCell(i).setCellValue(header.get(i));
                }
            } else {
                throw new JokoUtilsException("Empty data set");
            }

            Iterator<Integer> rows = data.rowKeySet().iterator();
            while (rows.hasNext()) {
                rowCount += 1;
                Row row = sheet.createRow(rowCount);
                Integer rowNumber = rows.next();
                Map<Integer, Object> currentRow = data.row(rowNumber);
                Iterator<Integer> rowDataIterator = currentRow.keySet().iterator();
                while (rowDataIterator.hasNext()) {
                    Integer colNumber = rowDataIterator.next();
                    registro = currentRow.get(colNumber);
                    // TODO: Format according cell type
                    if (registro instanceof Number) {
                        row.createCell(colNumber).setCellValue(
                                Double.parseDouble(registro.toString()));
                    } else if (registro instanceof Date) {
                        row.createCell(colNumber).setCellValue((Date) registro);
                    } else if (registro instanceof Boolean) {
                        row.createCell(colNumber).setCellValue((Boolean) registro);
                    } else {
                        row.createCell(colNumber).setCellValue(registro.toString());
                    }
                }// End column iteration
            }// End row iteration
            // 5. create excel file

            try (FileOutputStream fileOut = new FileOutputStream(fileName)){
                wb.write(fileOut);
            }
        } catch (IOException e) {
            throw new JokoUtilsException(e);
        }
    }

}
