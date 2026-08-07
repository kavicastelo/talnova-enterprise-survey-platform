package com.talnova.tesp.reportingservice.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class StreamingExcelWriterServiceImpl implements StreamingExcelWriterService {

    private static final Logger log = LoggerFactory.getLogger(StreamingExcelWriterServiceImpl.class);
    private static final int ROW_FLUSH_WINDOW = 100; // 100 row memory buffer per BR-RPT-004

    @Override
    public byte[] writeStreamingWorkbook(String sheetName, List<String> headers, List<List<Object>> rows) {
        log.info("Creating streaming XLSX workbook via SXSSFWorkbook({}) for sheet '{}' (rowCount: {}) per BR-RPT-004",
                ROW_FLUSH_WINDOW, sheetName, rows != null ? rows.size() : 0);

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(ROW_FLUSH_WINDOW);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            SXSSFSheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Survey Data");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Write Headers
            Row headerRow = sheet.createRow(0);
            if (headers != null) {
                for (int c = 0; c < headers.size(); c++) {
                    Cell cell = headerRow.createCell(c);
                    cell.setCellValue(headers.get(c));
                    cell.setCellStyle(headerStyle);
                }
            }

            // Write Data Rows
            if (rows != null) {
                int rowIndex = 1;
                for (List<Object> rowData : rows) {
                    Row row = sheet.createRow(rowIndex++);
                    if (rowData != null) {
                        for (int colIndex = 0; colIndex < rowData.size(); colIndex++) {
                            Cell cell = row.createCell(colIndex);
                            Object val = rowData.get(colIndex);
                            if (val instanceof Number n) {
                                cell.setCellValue(n.doubleValue());
                            } else if (val instanceof Boolean b) {
                                cell.setCellValue(b);
                            } else {
                                cell.setCellValue(val != null ? val.toString() : "");
                            }
                        }
                    }
                }
            }

            workbook.write(os);
            byte[] result = os.toByteArray();
            log.info("Successfully wrote streaming XLSX workbook (size: {} bytes)", result.length);
            return result;
        } catch (Exception e) {
            log.error("Failed to write streaming POI XLSX workbook", e);
            throw new RuntimeException("Streaming POI Excel export failed", e);
        }
    }
}
