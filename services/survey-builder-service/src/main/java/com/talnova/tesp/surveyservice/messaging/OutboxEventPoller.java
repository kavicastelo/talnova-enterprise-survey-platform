package com.talnova.tesp.surveyservice.messaging;

import com.talnova.tesp.surveyservice.domain.model.OutboxEventDocument;
import com.talnova.tesp.surveyservice.domain.model.OutboxStatus;
import com.talnova.tesp.surveyservice.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Scheduled component polling pending outbox events and relaying them asynchronously to Kafka topic 'tesp.survey.events.v1'.
 */
@Component
public class OutboxEventPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPoller.class);
    public static final String TOPIC_SURVEY_EVENTS = "tesp.survey.events.v1";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxEventPoller(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    public void processOutboxEvents() {
        List<OutboxEventDocument> pendingEvents = outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Processing {} pending survey outbox events for Kafka dispatch...", pendingEvents.size());
        for (OutboxEventDocument event : pendingEvents) {
            try {
                kafkaTemplate.send(TOPIC_SURVEY_EVENTS, event.getProjectId(), event.getPayload());
                event.setStatus(OutboxStatus.PROCESSED);
                event.setProcessedAt(Instant.now());
                outboxEventRepository.save(event);
                log.info("Successfully dispatched outbox event {} to Kafka topic {}", event.getEventId(), TOPIC_SURVEY_EVENTS);
            } catch (Exception ex) {
                log.error("Failed to dispatch outbox event {}: {}", event.getEventId(), ex.getMessage());
                event.setRetryCount(event.getRetryCount() + 1);
                if (event.getRetryCount() >= 3) {
                    event.setStatus(OutboxStatus.FAILED);
                }
                outboxEventRepository.save(event);
            }
        }
    }
}
