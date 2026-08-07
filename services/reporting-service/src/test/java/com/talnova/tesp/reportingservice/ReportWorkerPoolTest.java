package com.talnova.tesp.reportingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.queue.ReportWorkerPool;
import com.talnova.tesp.reportingservice.repository.ReportJobRepository;
import com.talnova.tesp.reportingservice.storage.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportWorkerPoolTest {

    @Mock
    private ReportJobRepository reportJobRepository;

    @Mock
    private S3StorageService s3StorageService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ListOperations<String, String> listOperations;

    private ObjectMapper objectMapper;
    private ReportWorkerPool workerPool;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        workerPool = new ReportWorkerPool(reportJobRepository, s3StorageService, redisTemplate, objectMapper);
    }

    @Test
    @DisplayName("TC-RPT-202-01: ReportWorkerPool executes compilation pipeline and transitions status to COMPLETED")
    void testProcessJobSuccess() {
        ReportJobDocument doc = ReportJobDocument.builder()
                .projectId("PRJ-99201")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .campaignId("CMP-1001")
                .requestedBy("USR-HR-001")
                .status(ReportStatus.QUEUED)
                .createdAt(Instant.now())
                .build();

        when(reportJobRepository.save(any(ReportJobDocument.class))).thenAnswer(i -> i.getArgument(0));
        when(s3StorageService.uploadArtifact(anyString(), any(), anyString())).thenReturn("PRJ-99201/CMP-1001/JOB-88102.pdf");
        when(s3StorageService.generatePreSignedDownloadUrl(anyString(), anyLong())).thenReturn("https://s3.amazonaws.com/tesp-report-exports/mock.pdf");

        boolean result = workerPool.processJob(doc);

        assertTrue(result);
        assertEquals(ReportStatus.COMPLETED, doc.getStatus());
        assertNotNull(doc.getDownloadUrl());
        assertNotNull(doc.getCompletedAt());
        verify(reportJobRepository, atLeast(2)).save(doc);
    }

    @Test
    @DisplayName("TC-RPT-202-02: ReportWorkerPool retries 3 times on failure then routes payload to Dead Letter Queue")
    void testProcessJobFailureRoutesToDlq() {
        ReportJobDocument doc = ReportJobDocument.builder()
                .projectId("PRJ-99201")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .campaignId("CMP-1001")
                .requestedBy("USR-HR-001")
                .status(ReportStatus.QUEUED)
                .createdAt(Instant.now())
                .build();

        when(reportJobRepository.save(any(ReportJobDocument.class))).thenAnswer(i -> i.getArgument(0));
        when(s3StorageService.uploadArtifact(anyString(), any(), anyString())).thenThrow(new RuntimeException("S3 connection error"));
        when(redisTemplate.opsForList()).thenReturn(listOperations);

        boolean result = workerPool.processJob(doc);

        assertFalse(result);
        assertEquals(ReportStatus.FAILED, doc.getStatus());
        assertNotNull(doc.getErrorMessage());
        verify(listOperations, times(1)).rightPush(eq(ReportWorkerPool.DLQ_KEY), anyString());
    }
}
