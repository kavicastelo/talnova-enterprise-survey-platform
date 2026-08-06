package com.talnova.tesp.ingestionservice;

import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import com.talnova.tesp.ingestionservice.event.ReactiveKafkaResponseProducerImpl;
import com.talnova.tesp.ingestionservice.event.SurveyResponseSubmittedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactiveKafkaResponseProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ReactiveKafkaResponseProducerImpl kafkaProducer;

    @BeforeEach
    void setUp() {
        kafkaProducer = new ReactiveKafkaResponseProducerImpl(kafkaTemplate);
    }

    @Test
    @DisplayName("TC-INT-401-01: Publish SurveyResponseSubmittedEvent to Kafka topic with projectId partition key")
    void testPublishResponseEvent() {
        SurveyResponseSubmittedEvent event = SurveyResponseSubmittedEvent.builder()
                .eventId("EVT-1001")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .responseId("RSP-881029")
                .respondentType(RespondentType.SEMI_ANONYMOUS)
                .nodeId("N-301")
                .answerCount(3)
                .build();

        Mono<Void> result = kafkaProducer.publishResponseEvent(event);

        StepVerifier.create(result)
                .verifyComplete();

        verify(kafkaTemplate, times(1)).send(
                eq(ReactiveKafkaResponseProducerImpl.RAW_RESPONSES_TOPIC),
                eq("PRJ-99201"),
                any(SurveyResponseSubmittedEvent.class)
        );
    }
}
