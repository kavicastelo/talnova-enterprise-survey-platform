package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.event.ReportGeneratedEvent;
import com.talnova.tesp.reportingservice.event.ReportEventPublisherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ReportEventPublisherServiceImpl publisherService;

    @BeforeEach
    void setUp() {
        publisherService = new ReportEventPublisherServiceImpl(kafkaTemplate);
    }

    @Test
    @DisplayName("TC-RPT-702-01: Publish ReportGeneratedEvent to Kafka topic tesp.reports.events.v1 per FR-RPT-008")
    void testPublishReportGeneratedEventSuccess() {
        Instant now = Instant.now();
        ReportJobDocument job = ReportJobDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .jobId("JOB-88102")
                .reportType(ReportType.EXEC_SUMMARY_PDF)
                .status(ReportStatus.COMPLETED)
                .downloadUrl("https://s3.amazonaws.com/tesp-report-exports/valid.pdf")
                .createdAt(now)
                .expiresAt(now.plusSeconds(86400L))
                .build();

        publisherService.publishReportGeneratedEvent(job, "hr.director@acme.org");

        verify(kafkaTemplate, times(1)).send(
                eq(ReportEventPublisherServiceImpl.TOPIC_NAME),
                eq("PRJ-99201"),
                any(ReportGeneratedEvent.class)
        );
    }
}
