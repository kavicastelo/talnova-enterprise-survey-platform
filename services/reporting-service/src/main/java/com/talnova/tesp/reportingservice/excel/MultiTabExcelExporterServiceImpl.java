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
public class MultiTabExcelExporterServiceImpl implements MultiTabExcelExporterService {

    private static final Logger log = LoggerFactory.getLogger(MultiTabExcelExporterServiceImpl.class);
    private static final int ROW_FLUSH_WINDOW = 100;

    @Override
    public byte[] generateMultiTabReport(List<List<Object>> rawResponses, List<List<Object>> crossTabMatrix) {
        log.info("Exporting multi-tab XLSX report (Sheet 1: Raw Responses, Sheet 2: Cross-Tab Matrix) per FR-RPT-003");

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(ROW_FLUSH_WINDOW);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Sheet 1: Anonymized Raw Responses (0 PII identity columns)
            SXSSFSheet sheet1 = workbook.createSheet("Anonymized Raw Responses");
            List<String> sheet1Headers = List.of("Response Hash ID", "Question Group", "Question Text", "Score", "Demographic Node");
            writeSheetData(sheet1, headerStyle, sheet1Headers, rawResponses, false);

            // Sheet 2: Department Cross-Tabulation (N < 5 suppression enforced)
            SXSSFSheet sheet2 = workbook.createSheet("Department Cross-Tabulation");
            List<String> sheet2Headers = List.of("Organization Node", "Total Responses (N)", "Mean Score", "Promoters %", "Passives %", "Detractors %");
            writeSheetData(sheet2, headerStyle, sheet2Headers, crossTabMatrix, true);

            workbook.write(os);
            byte[] xlsxBytes = os.toByteArray();
            log.info("Successfully generated multi-tab XLSX workbook (size: {} bytes)", xlsxBytes.length);
            return xlsxBytes;
        } catch (Exception e) {
            log.error("Failed to generate multi-tab Excel report", e);
            throw new RuntimeException("Multi-tab Excel export failed", e);
        }
    }

    private void writeSheetData(SXSSFSheet sheet, CellStyle headerStyle, List<String> headers, List<List<Object>> rows, boolean enforceAnonymity) {
        Row headerRow = sheet.createRow(0);
        for (int c = 0; c < headers.size(); c++) {
            Cell cell = headerRow.createCell(c);
            cell.setCellValue(headers.get(c));
            cell.setCellStyle(headerStyle);
        }

        if (rows != null) {
            int rowIndex = 1;
            for (List<Object> rowData : rows) {
                Row row = sheet.createRow(rowIndex++);
                if (rowData != null) {
                    // Check if N < 5 for cross-tab sheet suppression
                    boolean isSuppressed = false;
                    if (enforceAnonymity && rowData.size() > 1 && rowData.get(1) instanceof Number count) {
                        if (count.intValue() < 5) {
                            isSuppressed = true;
                        }
                    }

                    for (int colIndex = 0; colIndex < rowData.size(); colIndex++) {
                        Cell cell = row.createCell(colIndex);
                        Object val = rowData.get(colIndex);

                        if (isSuppressed && colIndex >= 2) {
                            cell.setCellValue("* N/A (N < 5)");
                        } else if (val instanceof Number n) {
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
    }
}
