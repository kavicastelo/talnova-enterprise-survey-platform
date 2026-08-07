package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.storage.ReportArtifactUploaderServiceImpl;
import com.talnova.tesp.reportingservice.storage.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportArtifactUploaderTest {

    @Mock
    private S3StorageService s3StorageService;

    private ReportArtifactUploaderServiceImpl uploaderService;

    @BeforeEach
    void setUp() {
        uploaderService = new ReportArtifactUploaderServiceImpl(s3StorageService);
    }

    @Test
    @DisplayName("TC-RPT-601-01: Upload report binary artifact to S3 bucket under path /{projectId}/{campaignId}/{jobId}.pdf per FR-RPT-006")
    void testUploadReportArtifactPdf() {
        ReportJobDocument job = ReportJobDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .status(ReportStatus.PROCESSING)
                .createdAt(Instant.now())
                .build();

        byte[] pdfBytes = "%PDF-1.4 Mock Binary Data".getBytes();
        String expectedKey = "PRJ-99201/CMP-1001/JOB-88102.pdf";

        when(s3StorageService.uploadArtifact(eq(expectedKey), eq(pdfBytes), eq("application/pdf"))).thenReturn(expectedKey);

        String resultKey = uploaderService.uploadReportArtifact(job, pdfBytes);

        assertEquals(expectedKey, resultKey);
        verify(s3StorageService, times(1)).uploadArtifact(expectedKey, pdfBytes, "application/pdf");
    }
}
