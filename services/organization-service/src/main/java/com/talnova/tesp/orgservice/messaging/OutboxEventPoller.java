package com.talnova.tesp.orgservice.messaging;

import com.talnova.tesp.orgservice.domain.OutboxEventDocument;
import com.talnova.tesp.orgservice.domain.OutboxStatus;
import com.talnova.tesp.orgservice.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@EnableScheduling
public class OutboxEventPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPoller.class);

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topics.org-events:tesp.org.events.v1}")
    private String orgEventsTopic;

    public OutboxEventPoller(OutboxEventRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    public void processOutboxEvents() {
        List<OutboxEventDocument> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Polling {} pending organization outbox events for publishing...", pendingEvents.size());

        for (OutboxEventDocument event : pendingEvents) {
            try {
                String partitionKey = event.getProjectId() != null ? event.getProjectId() : event.getAggregateId();
                kafkaTemplate.send(orgEventsTopic, partitionKey, event.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                event.setStatus(OutboxStatus.PUBLISHED);
                                event.setProcessedAt(Instant.now());
                                outboxRepository.save(event);
                                log.info("Successfully published outbox event {} to Kafka topic {}", event.getEventId(), orgEventsTopic);
                            } else {
                                event.setStatus(OutboxStatus.FAILED);
                                outboxRepository.save(event);
                                log.error("Failed to publish outbox event {}: {}", event.getEventId(), ex.getMessage());
                            }
                        });
            } catch (Exception ex) {
                event.setStatus(OutboxStatus.FAILED);
                outboxRepository.save(event);
                log.error("Error dispatching outbox event {}: {}", event.getEventId(), ex.getMessage());
            }
        }
    }
}
