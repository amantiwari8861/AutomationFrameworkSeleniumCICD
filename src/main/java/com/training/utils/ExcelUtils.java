package com.training.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public final class ExcelUtils {

    private ExcelUtils() { // singelton design pattern
    }

    public static Object[][] readSheet(String resourcePath,String sheetName,boolean skipHeader) throws IOException {

        try (InputStream input = ExcelUtils.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (input == null) {
                throw new IllegalArgumentException(
                    "Excel file not found in classpath: " + resourcePath);
            }

            try (Workbook workbook = WorkbookFactory.create(input)) {

                Sheet sheet = workbook.getSheet(sheetName);

                if (sheet == null) {
                    throw new IllegalArgumentException(
                        "Sheet not found: " + sheetName);
                }

                DataFormatter formatter = new DataFormatter();
         FormulaEvaluator evaluator =workbook.getCreationHelper().createFormulaEvaluator();

                int firstRow = sheet.getFirstRowNum();
                int lastRow = sheet.getLastRowNum();

                if (skipHeader) {
                    firstRow++;
                }

                List<Object[]> data = new ArrayList<>();

                for (int rowIndex = firstRow;rowIndex <= lastRow;rowIndex++) {

                    Row row = sheet.getRow(rowIndex);

                    if (row == null) {
                        continue;
                    }

                    int lastCell = row.getLastCellNum();

                    if (lastCell < 0) {
                        continue;
                    }

                    Object[] rowData = new Object[lastCell];
                    boolean emptyRow = true;

                    for (int cellIndex = 0; cellIndex < lastCell; cellIndex++) {

                        Cell cell = row.getCell(
                                cellIndex,
                                Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                        String value = cell == null
                                ? ""
                                : formatter.formatCellValue(cell, evaluator);

                        rowData[cellIndex] = value;

                        if (!value.isBlank()) {
                            emptyRow = false;
                        }
                    }

                    // Ignore completely empty rows
                    if (!emptyRow) {
                        data.add(rowData);
                    }
                }

                return data.toArray(new Object[0][]);
            }
        }
    }
}