package com.talnova.tesp.configservice.messaging;

import com.talnova.tesp.configservice.domain.OutboxEventDocument;
import com.talnova.tesp.configservice.domain.OutboxStatus;
import com.talnova.tesp.configservice.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxEventPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPoller.class);
    public static final String TOPIC_PROJECT_EVENTS = "tesp.project.events.v1";

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

        log.info("Processing {} pending outbox events for Kafka dispatch...", pendingEvents.size());
        for (OutboxEventDocument event : pendingEvents) {
            try {
                kafkaTemplate.send(TOPIC_PROJECT_EVENTS, event.getProjectId(), event.getPayload());
                event.setStatus(OutboxStatus.PROCESSED);
                event.setProcessedAt(Instant.now());
                outboxEventRepository.save(event);
                log.info("Successfully dispatched outbox event {} to Kafka topic {}", event.getEventId(), TOPIC_PROJECT_EVENTS);
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
