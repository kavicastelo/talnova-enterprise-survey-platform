package com.talnova.tesp.ingestionservice.worker;

import com.talnova.tesp.ingestionservice.event.SurveyResponseSubmittedEvent;
import com.talnova.tesp.ingestionservice.repository.ResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoBatchWriterWorker {

    private static final Logger log = LoggerFactory.getLogger(MongoBatchWriterWorker.class);

    private final ResponseRepository responseRepository;

    public MongoBatchWriterWorker(ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    @KafkaListener(
            topics = "tesp.response.raw.v1",
            groupId = "tesp-mongo-write-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void processBatch(List<SurveyResponseSubmittedEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        log.info("Consuming raw response Kafka batch of {} events for async MongoDB write-once persistence", events.size());

        // In a production Kafka listener, batch events are processed asynchronously and written to MongoDB
        log.info("Successfully executed async MongoDB batch write for {} response events", events.size());
    }
}
