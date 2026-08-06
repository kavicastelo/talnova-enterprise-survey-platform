package com.talnova.tesp.distservice.messaging;

import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.NotificationDispatchEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationKafkaProducerImpl implements NotificationKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationKafkaProducerImpl.class);
    public static final String DISPATCH_EVENTS_TOPIC = "tesp.distribution.dispatch-events.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotificationKafkaProducerImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public NotificationDispatchEventDTO toEventDTO(ChannelMessageDispatchDTO dispatch) {
        return NotificationDispatchEventDTO.builder()
                .eventId("EVT-DISP-" + UUID.randomUUID().toString().substring(0, 8))
                .projectId(dispatch.getProjectId())
                .campaignId(dispatch.getCampaignId())
                .employeeId(dispatch.getEmployeeId())
                .recipientContact(dispatch.getRecipientContact())
                .channel(dispatch.getChannel())
                .subject(dispatch.getSubject())
                .bodyText(dispatch.getBodyText())
                .surveyUrl(dispatch.getSurveyUrl())
                .kioskPin(dispatch.getKioskPin())
                .timestamp(Instant.now())
                .build();
    }

    @Override
    public void publishDispatchEvent(ChannelMessageDispatchDTO dispatch) {
        NotificationDispatchEventDTO event = toEventDTO(dispatch);
        String partitionKey = dispatch.getCampaignId();
        log.info("Publishing Kafka dispatch event '{}' to topic '{}' with key '{}'", event.getEventId(), DISPATCH_EVENTS_TOPIC, partitionKey);

        try {
            kafkaTemplate.send(DISPATCH_EVENTS_TOPIC, partitionKey, event);
        } catch (Exception e) {
            log.error("Failed to publish dispatch event to Kafka topic {}: {}", DISPATCH_EVENTS_TOPIC, e.getMessage(), e);
        }
    }

    @Override
    public void publishBatchDispatchEvents(List<ChannelMessageDispatchDTO> dispatches) {
        if (dispatches == null || dispatches.isEmpty()) {
            return;
        }

        log.info("Publishing batch of {} dispatch events to Kafka topic '{}'", dispatches.size(), DISPATCH_EVENTS_TOPIC);
        for (ChannelMessageDispatchDTO dispatch : dispatches) {
            publishDispatchEvent(dispatch);
        }
    }
}
