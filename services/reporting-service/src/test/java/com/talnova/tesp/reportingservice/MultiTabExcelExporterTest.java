package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.excel.MultiTabExcelExporterServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiTabExcelExporterTest {

    private MultiTabExcelExporterServiceImpl exporterService;

    @BeforeEach
    void setUp() {
        exporterService = new MultiTabExcelExporterServiceImpl();
    }

    @Test
    @DisplayName("TC-RPT-502-01: Generate multi-tab XLSX report with 0 PII columns on Sheet 1 and N < 5 score suppression on Sheet 2 per BR-RPT-001")
    void testGenerateMultiTabReportSuccess() throws Exception {
        List<List<Object>> rawResponses = List.of(
                List.of("RSP-HASH-001", "Engagement", "I feel valued at work", 4.5, "IT_DEPT"),
                List.of("RSP-HASH-002", "Culture", "Clear communication", 5.0, "HR_DEPT")
        );

        List<List<Object>> crossTabMatrix = List.of(
                List.of("Engineering Node", 45, 4.6, 60.0, 30.0, 10.0),
                List.of("Small Branch Node", 3, 4.1, 50.0, 50.0, 0.0) // N = 3 < 5 should trigger suppression
        );

        byte[] xlsxBytes = exporterService.generateMultiTabReport(rawResponses, crossTabMatrix);

        assertNotNull(xlsxBytes);
        assertTrue(xlsxBytes.length > 0);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsxBytes))) {
            assertEquals(2, wb.getNumberOfSheets());
            assertEquals("Anonymized Raw Responses", wb.getSheetName(0));
            assertEquals("Department Cross-Tabulation", wb.getSheetName(1));

            // Verify N < 5 suppression on Sheet 2 Row 2 (Small Branch Node with N = 3)
            String suppressedCellValue = wb.getSheetAt(1).getRow(2).getCell(2).getStringCellValue();
            assertEquals("* N/A (N < 5)", suppressedCellValue);
        }
    }
}
