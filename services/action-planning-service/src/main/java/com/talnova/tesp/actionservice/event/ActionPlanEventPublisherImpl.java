package com.talnova.tesp.actionservice.event;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ActionPlanEventPublisherImpl implements ActionPlanEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ActionPlanEventPublisherImpl.class);
    public static final String TOPIC_NAME = "tesp.action.events.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public ActionPlanEventPublisherImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishStatusChangedEvent(ActionPlanDocument plan, double scoreDelta) {
        String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant now = Instant.now();

        ActionPlanStatusChangedEvent event = ActionPlanStatusChangedEvent.builder()
                .eventId(eventId)
                .actionPlanId(plan.getActionPlanId())
                .projectId(plan.getProjectId())
                .nodeId(plan.getNodeId())
                .newStatus(plan.getStatus())
                .postActionScore(plan.getPostActionScore())
                .scoreDelta(scoreDelta)
                .timestamp(now)
                .build();

        log.info("Dispatched ActionPlanStatusChangedEvent (EventID: {}, ActionPlanID: {}, Status: {}) to Kafka topic '{}' per FR-ACT-008",
                eventId, plan.getActionPlanId(), plan.getStatus(), TOPIC_NAME);

        try {
            kafkaTemplate.send(TOPIC_NAME, plan.getProjectId(), event);
        } catch (Exception e) {
            log.error("Failed to publish ActionPlanStatusChangedEvent for actionPlanId '{}' to Kafka topic '{}'",
                    plan.getActionPlanId(), TOPIC_NAME, e);
        }
    }
}
