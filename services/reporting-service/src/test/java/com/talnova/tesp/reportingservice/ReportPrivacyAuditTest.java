package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.dto.ExecutiveReportDataDTO;
import com.talnova.tesp.reportingservice.dto.ReportJobResponseDTO;
import com.talnova.tesp.reportingservice.excel.MultiTabExcelExporterServiceImpl;
import com.talnova.tesp.reportingservice.excel.StreamingExcelWriterServiceImpl;
import com.talnova.tesp.reportingservice.pdf.ChromiumPdfRendererServiceImpl;
import com.talnova.tesp.reportingservice.security.ReportDownloadLinkGuardServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportPrivacyAuditTest {

    private ReportDownloadLinkGuardServiceImpl linkGuardService;
    private MultiTabExcelExporterServiceImpl multiTabExporter;
    private StreamingExcelWriterServiceImpl streamingWriter;
    private ChromiumPdfRendererServiceImpl pdfRenderer;

    @BeforeEach
    void setUp() {
        linkGuardService = new ReportDownloadLinkGuardServiceImpl();
        multiTabExporter = new MultiTabExcelExporterServiceImpl();
        streamingWriter = new StreamingExcelWriterServiceImpl();
        pdfRenderer = new ChromiumPdfRendererServiceImpl();
    }

    @Test
    @DisplayName("TC-RPT-001 / Security Audit: Enforce 24-hour pre-signed download URL expiration guard per BR-RPT-003")
    void testPreSignedUrlExpirationSecurityAudit() {
        Instant past = Instant.now().minusSeconds(10L);
        ReportJobDocument expiredJob = ReportJobDocument.builder()
                .projectId("PRJ-99201")
                .jobId("JOB-EXPIRED-001")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .status(ReportStatus.COMPLETED)
                .downloadUrl("https://s3.amazonaws.com/tesp-report-exports/expired.pdf")
                .createdAt(past.minusSeconds(86400L))
                .expiresAt(past)
                .build();

        ReportJobResponseDTO response = linkGuardService.validateAndSecureDownloadLink(expiredJob);

        assertNull(response.getDownloadUrl(), "Expired pre-signed URL must be revoked");
        assertTrue(response.getMessage().contains("expired after 24 hours"));
    }

    @Test
    @DisplayName("TC-RPT-002 / Privacy Audit: Enforce 100% N < 5 anonymity suppression in Excel cross-tab matrix per BR-RPT-001")
    void testAnonymitySuppressionPrivacyAudit() throws Exception {
        List<List<Object>> rawResponses = List.of(
                List.of("RSP-001", "Culture", "Great workplace", 5.0, "SALES")
        );

        List<List<Object>> crossTabMatrix = List.of(
                List.of("Large Node", 100, 4.8, 80.0, 15.0, 5.0),
                List.of("Small Branch Node", 2, 4.0, 50.0, 50.0, 0.0) // N = 2 < 5 triggers suppression
        );

        byte[] xlsxBytes = multiTabExporter.generateMultiTabReport(rawResponses, crossTabMatrix);
        assertNotNull(xlsxBytes);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsxBytes))) {
            // Check Sheet 2 Row 2 (Small Branch Node N=2)
            String cellVal = wb.getSheetAt(1).getRow(2).getCell(2).getStringCellValue();
            assertEquals("* N/A (N < 5)", cellVal, "Numerical score for N < 5 group must be suppressed to '* N/A (N < 5)'");
        }
    }

    @Test
    @DisplayName("SLA-RPT-02 / Load Benchmark: Stream 50,000 response rows via SXSSFWorkbook(100) buffer without OutOfMemoryError per BR-RPT-004")
    void test50kRowStreamingExcelExportLoadBenchmark() {
        List<String> headers = List.of("Response Hash", "Question Group", "Score", "Demographic Tag");
        List<List<Object>> rows = new ArrayList<>();

        for (int i = 1; i <= 50000; i++) {
            rows.add(List.of("RSP-HASH-" + i, "Organizational Culture", 4.2, "ENG-DEPT"));
        }

        long startTime = System.currentTimeMillis();
        byte[] xlsxBytes = streamingWriter.writeStreamingWorkbook("50k Raw Responses", headers, rows);
        long duration = System.currentTimeMillis() - startTime;

        assertNotNull(xlsxBytes);
        assertTrue(xlsxBytes.length > 0);
        assertTrue(duration < 10000, "50k row streaming export took " + duration + " ms (Expected < 10000 ms)");
    }
}
