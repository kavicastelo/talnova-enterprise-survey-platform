package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.domain.model.RiskSeverity;
import com.talnova.tesp.aiservice.event.RiskAlertPublisherServiceImpl;
import com.talnova.tesp.aiservice.event.WorkplaceRiskAlertEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RiskAlertPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private RiskAlertPublisherServiceImpl riskAlertPublisher;

    @Test
    @DisplayName("TC-AI-402-01: Publish WorkplaceRiskAlertEvent to Kafka topic tesp.notifications.queue.v1 per BR-AI-004")
    void testPublishRiskAlert() {
        WorkplaceRiskAlertEvent event = WorkplaceRiskAlertEvent.builder()
                .alertId("ALT-9901")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .responseId("RESP-8820")
                .questionId("Q-104")
                .category("SAFETY")
                .severity(RiskSeverity.CRITICAL)
                .detectedKeyword("safety guard broken")
                .sanitizedSnippet("Safety guard broken on machine")
                .detectedAt(Instant.now())
                .build();

        riskAlertPublisher.publishRiskAlert(event);

        verify(kafkaTemplate, times(1)).send(
                eq(RiskAlertPublisherServiceImpl.NOTIFICATIONS_TOPIC),
                eq("PRJ-99201"),
                eq(event)
        );
    }
}
