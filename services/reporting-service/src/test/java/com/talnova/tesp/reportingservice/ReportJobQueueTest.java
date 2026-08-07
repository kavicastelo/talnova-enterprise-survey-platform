package com.talnova.tesp.reportingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.queue.ReportJobQueueProducerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportJobQueueTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ListOperations<String, String> listOperations;

    private ObjectMapper objectMapper;
    private ReportJobQueueProducerServiceImpl producerService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        producerService = new ReportJobQueueProducerServiceImpl(redisTemplate, objectMapper);
    }

    @Test
    @DisplayName("TC-RPT-201-01: Enqueue report job payload to Redis list tesp:reports:queue per FR-RPT-001")
    void testEnqueueJob() {
        ReportJobDocument doc = ReportJobDocument.builder()
                .projectId("PRJ-99201")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .campaignId("CMP-1001")
                .requestedBy("USR-HR-001")
                .status(ReportStatus.QUEUED)
                .createdAt(Instant.now())
                .build();

        producerService.enqueueJob(doc);

        verify(listOperations, times(1)).rightPush(eq("tesp:reports:queue"), anyString());
    }
}
