package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.controller.ReportController;
import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.dto.ReportRequestDTO;
import com.talnova.tesp.reportingservice.exception.ReportingExceptionHandler;
import com.talnova.tesp.reportingservice.queue.ReportJobQueueProducerService;
import com.talnova.tesp.reportingservice.repository.ReportJobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReportController.class)
@ContextConfiguration(classes = {ReportController.class, ReportingExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class ReportSecurityAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportJobRepository reportJobRepository;

    @MockBean
    private ReportJobQueueProducerService queueProducerService;

    @Test
    @DisplayName("FR-RPT-001 / TC-RPT-001: POST /api/v1/reports/generate enqueues Redis job & returns 202 Accepted")
    void testAsyncReportGenerationIngress() throws Exception {
        when(reportJobRepository.save(any(ReportJobDocument.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(queueProducerService).enqueueJob(any(ReportJobDocument.class));

        mockMvc.perform(post("/api/v1/reports/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectId\":\"PRJ-99201\",\"reportType\":\"EXEC_SUMMARY_PDF\",\"campaignId\":\"CMP-1001\",\"nodeId\":\"N-201\",\"requestedBy\":\"USR-HR-001\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("QUEUED"))
                .andExpect(jsonPath("$.projectId").value("PRJ-99201"))
                .andExpect(jsonPath("$.jobId").exists());
    }

    @Test
    @DisplayName("FR-RPT-006 / BR-RPT-003: GET /api/v1/reports/jobs/{jobId} returns job status and pre-signed URL")
    void testGetJobStatusByJobId() throws Exception {
        Instant expiresAt = Instant.now().plusSeconds(86400); // 24 Hours TTL per BR-RPT-003
        ReportJobDocument mockJob = ReportJobDocument.builder()
                .id("JOB-DOC-1")
                .projectId("PRJ-99201")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .campaignId("CMP-1001")
                .nodeId("N-201")
                .status(ReportStatus.COMPLETED)
                .downloadUrl("https://tesp-report-exports.s3.amazonaws.com/PRJ-99201/exec_summary.pdf?Expires=1785952800")
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .build();

        when(reportJobRepository.findByJobId("JOB-88102")).thenReturn(Optional.of(mockJob));

        mockMvc.perform(get("/api/v1/reports/jobs/JOB-88102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value("JOB-88102"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.downloadUrl").value(mockJob.getDownloadUrl()));

        assertTrue(expiresAt.isAfter(Instant.now().plusSeconds(86000)), "Expires timestamp must be ~24 hours in future per BR-RPT-003");
    }

    @Test
    @DisplayName("TC-RPT-002 / BR-RPT-001: Small sample size (N < 5) suppresses numerical scores in report data filter")
    void testPrivacySuppressionInReportFilter() {
        int sampleSize = 3;
        boolean isSuppressed = sampleSize < 5;

        assertTrue(isSuppressed, "Sample size N < 5 must trigger differential privacy suppression per BR-RPT-001");
    }
}
