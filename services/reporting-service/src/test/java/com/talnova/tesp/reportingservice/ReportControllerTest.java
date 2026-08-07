package com.talnova.tesp.reportingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.reportingservice.controller.ReportController;
import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.dto.ReportRequestDTO;
import com.talnova.tesp.reportingservice.exception.ReportingExceptionHandler;
import com.talnova.tesp.reportingservice.queue.ReportJobQueueProducerService;
import com.talnova.tesp.reportingservice.repository.ReportJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportJobRepository reportJobRepository;

    @Mock
    private ReportJobQueueProducerService queueProducerService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        ReportController controller = new ReportController(reportJobRepository, queueProducerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ReportingExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-RPT-201-02: POST /api/v1/reports/generate returns 202 Accepted with jobId per FR-RPT-001")
    void testGenerateReportSuccess() throws Exception {
        ReportRequestDTO request = ReportRequestDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .nodeId("N-201")
                .requestedBy("USR-HR-001")
                .build();

        doNothing().when(queueProducerService).enqueueJob(any(ReportJobDocument.class));
        when(reportJobRepository.save(any(ReportJobDocument.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/v1/reports/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("QUEUED"))
                .andExpect(jsonPath("$.jobId").exists())
                .andExpect(jsonPath("$.projectId").value("PRJ-99201"));

        verify(reportJobRepository, times(1)).save(any(ReportJobDocument.class));
        verify(queueProducerService, times(1)).enqueueJob(any(ReportJobDocument.class));
    }

    @Test
    @DisplayName("TC-RPT-201-03: GET /api/v1/reports/jobs/{projectId}/{jobId} fetches report job status")
    void testGetJobStatusSuccess() throws Exception {
        ReportJobDocument doc = ReportJobDocument.builder()
                .projectId("PRJ-99201")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .campaignId("CMP-1001")
                .nodeId("N-201")
                .requestedBy("USR-HR-001")
                .status(ReportStatus.QUEUED)
                .createdAt(Instant.now())
                .build();

        when(reportJobRepository.findByProjectIdAndJobId("PRJ-99201", "JOB-88102")).thenReturn(Optional.of(doc));

        mockMvc.perform(get("/api/v1/reports/jobs/PRJ-99201/JOB-88102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value("JOB-88102"))
                .andExpect(jsonPath("$.status").value("QUEUED"));
    }
}
