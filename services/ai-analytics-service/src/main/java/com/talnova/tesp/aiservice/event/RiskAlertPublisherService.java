package com.talnova.tesp.aiservice.event;

public interface RiskAlertPublisherService {

    /**
     * Publishes WorkplaceRiskAlertEvent to Kafka topic 'tesp.notifications.queue.v1' per BR-AI-004.
     */
    void publishRiskAlert(WorkplaceRiskAlertEvent event);
}
