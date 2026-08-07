package com.talnova.tesp.reportingservice.event;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ReportEventPublisherServiceImpl implements ReportEventPublisherService {

    private static final Logger log = LoggerFactory.getLogger(ReportEventPublisherServiceImpl.class);
    public static final String TOPIC_NAME = "tesp.reports.events.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public ReportEventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishReportGeneratedEvent(ReportJobDocument job, String recipientEmail) {
        String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant now = Instant.now();

        ReportGeneratedEvent event = ReportGeneratedEvent.builder()
                .eventId(eventId)
                .projectId(job.getProjectId())
                .campaignId(job.getCampaignId())
                .jobId(job.getJobId())
                .reportType(job.getReportType())
                .downloadUrl(job.getDownloadUrl())
                .expiresAt(job.getExpiresAt())
                .recipientEmail(recipientEmail)
                .timestamp(now)
                .build();

        log.info("Dispatched ReportGeneratedEvent (EventID: {}, JobID: {}) to Kafka topic '{}' for recipient '{}' per FR-RPT-008",
                eventId, job.getJobId(), TOPIC_NAME, recipientEmail);

        try {
            kafkaTemplate.send(TOPIC_NAME, job.getProjectId(), event);
        } catch (Exception e) {
            log.error("Failed to publish ReportGeneratedEvent for jobId '{}' to Kafka topic '{}'", job.getJobId(), TOPIC_NAME, e);
        }
    }
}
