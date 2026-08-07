package com.talnova.tesp.aiservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RiskAlertPublisherServiceImpl implements RiskAlertPublisherService {

    private static final Logger log = LoggerFactory.getLogger(RiskAlertPublisherServiceImpl.class);
    public static final String NOTIFICATIONS_TOPIC = "tesp.notifications.queue.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RiskAlertPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishRiskAlert(WorkplaceRiskAlertEvent event) {
        if (event == null) return;
        try {
            kafkaTemplate.send(NOTIFICATIONS_TOPIC, event.getProjectId(), event);
            log.info("Dispatched WorkplaceRiskAlertEvent (AlertID: {}, Category: {}, Severity: {}) to Kafka topic '{}' per BR-AI-004",
                    event.getAlertId(), event.getCategory(), event.getSeverity(), NOTIFICATIONS_TOPIC);
        } catch (Exception e) {
            log.error("Failed to publish WorkplaceRiskAlertEvent to Kafka topic '{}'", NOTIFICATIONS_TOPIC, e);
        }
    }
}
