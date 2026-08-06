package com.talnova.tesp.ingestionservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveKafkaResponseProducerImpl implements ReactiveKafkaResponseProducer {

    private static final Logger log = LoggerFactory.getLogger(ReactiveKafkaResponseProducerImpl.class);
    public static final String RAW_RESPONSES_TOPIC = "tesp.response.raw.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ReactiveKafkaResponseProducerImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Mono<Void> publishResponseEvent(SurveyResponseSubmittedEvent event) {
        return Mono.fromRunnable(() -> {
            log.info("Publishing raw response event '{}' for responseId '{}' to Kafka topic '{}' with key '{}'",
                    event.getEventId(), event.getResponseId(), RAW_RESPONSES_TOPIC, event.getProjectId());
            kafkaTemplate.send(RAW_RESPONSES_TOPIC, event.getProjectId(), event);
        });
    }
}
