package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReportJobRepositoryTest {

    @Test
    @DisplayName("TC-RPT-101-01: Build and verify ReportJobDocument model fields and enums")
    void testReportJobDocumentBuilder() {
        Instant now = Instant.now();

        ReportJobDocument doc = ReportJobDocument.builder()
                .id("DOC-001")
                .projectId("PRJ-99201")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .campaignId("CMP-1001")
                .nodeId("N-201")
                .requestedBy("USR-HR-001")
                .status(ReportStatus.QUEUED)
                .createdAt(now)
                .build();

        assertNotNull(doc);
        assertEquals("PRJ-99201", doc.getProjectId());
        assertEquals("JOB-88102", doc.getJobId());
        assertEquals(ReportType.EXEC_SUMMARY_PDF, doc.getReportType());
        assertEquals(ReportStatus.QUEUED, doc.getStatus());
        assertEquals("N-201", doc.getNodeId());
    }
}
