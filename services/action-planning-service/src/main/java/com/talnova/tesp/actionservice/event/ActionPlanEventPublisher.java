package com.talnova.tesp.actionservice.event;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;

public interface ActionPlanEventPublisher {

    /**
     * Publishes ActionPlanStatusChangedEvent to Kafka topic 'tesp.action.events.v1' per FR-ACT-008.
     */
    void publishStatusChangedEvent(ActionPlanDocument plan, double scoreDelta);
}
