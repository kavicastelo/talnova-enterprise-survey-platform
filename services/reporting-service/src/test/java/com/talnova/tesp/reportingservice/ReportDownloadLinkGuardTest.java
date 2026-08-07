package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.dto.ReportJobResponseDTO;
import com.talnova.tesp.reportingservice.security.ReportDownloadLinkGuardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReportDownloadLinkGuardTest {

    private ReportDownloadLinkGuardServiceImpl guardService;

    @BeforeEach
    void setUp() {
        guardService = new ReportDownloadLinkGuardServiceImpl();
    }

    @Test
    @DisplayName("TC-RPT-602-01: Valid pre-signed download link within 24h window returns active URL")
    void testValidateActiveDownloadLink() {
        Instant now = Instant.now();
        ReportJobDocument job = ReportJobDocument.builder()
                .projectId("PRJ-99201")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .status(ReportStatus.COMPLETED)
                .downloadUrl("https://s3.amazonaws.com/tesp-report-exports/valid.pdf")
                .createdAt(now)
                .expiresAt(now.plusSeconds(86400L)) // 24 hours in future
                .build();

        ReportJobResponseDTO response = guardService.validateAndSecureDownloadLink(job);

        assertNotNull(response.getDownloadUrl());
        assertEquals("Download link active", response.getMessage());
    }

    @Test
    @DisplayName("TC-RPT-602-02: Expired pre-signed download link (> 24h) revokes URL access per BR-RPT-003")
    void testValidateExpiredDownloadLinkRevocation() {
        Instant past = Instant.now().minusSeconds(3600L); // 1 hour ago
        ReportJobDocument job = ReportJobDocument.builder()
                .projectId("PRJ-99201")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .status(ReportStatus.COMPLETED)
                .downloadUrl("https://s3.amazonaws.com/tesp-report-exports/expired.pdf")
                .createdAt(past.minusSeconds(86400L))
                .expiresAt(past) // Expired!
                .build();

        ReportJobResponseDTO response = guardService.validateAndSecureDownloadLink(job);

        assertNull(response.getDownloadUrl(), "Expired download URL must be revoked and set to null");
        assertTrue(response.getMessage().contains("expired after 24 hours"));
    }
}
