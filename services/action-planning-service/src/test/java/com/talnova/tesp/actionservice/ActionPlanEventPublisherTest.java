package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.event.ActionPlanEventPublisherImpl;
import com.talnova.tesp.actionservice.event.ActionPlanStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionPlanEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ActionPlanEventPublisherImpl publisherService;

    @BeforeEach
    void setUp() {
        publisherService = new ActionPlanEventPublisherImpl(kafkaTemplate);
    }

    @Test
    @DisplayName("TC-ACT-702-01: Publish ActionPlanStatusChangedEvent to Kafka topic tesp.action.events.v1 per FR-ACT-008")
    void testPublishStatusChangedEventSuccess() {
        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .status(ActionStatus.VERIFIED)
                .postActionScore(70.0)
                .build();

        publisherService.publishStatusChangedEvent(plan, 16.0);

        verify(kafkaTemplate, times(1)).send(
                eq(ActionPlanEventPublisherImpl.TOPIC_NAME),
                eq("PRJ-99201"),
                any(ActionPlanStatusChangedEvent.class)
        );
    }
}
