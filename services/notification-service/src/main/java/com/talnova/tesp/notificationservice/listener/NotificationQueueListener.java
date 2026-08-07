package com.talnova.tesp.notificationservice.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.notificationservice.domain.model.NotificationChannel;
import com.talnova.tesp.notificationservice.dto.NotificationRequestDTO;
import com.talnova.tesp.notificationservice.service.NotificationDispatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationQueueListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationQueueListener.class);
    public static final String NOTIFICATIONS_TOPIC = "tesp.notifications.queue.v1";

    private final NotificationDispatcherService dispatcherService;
    private final ObjectMapper objectMapper;

    public NotificationQueueListener(NotificationDispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    @KafkaListener(topics = NOTIFICATIONS_TOPIC, groupId = "tesp-notification-group")
    public void consumeNotificationEvent(String message) {
        log.info("Received raw notification event payload from Kafka topic '{}'", NOTIFICATIONS_TOPIC);
        try {
            JsonNode root = objectMapper.readTree(message);
            String projectId = root.has("projectId") ? root.get("projectId").asText() : "PRJ-DEFAULT";
            String recipient = root.has("recipient") ? root.get("recipient").asText() :
                              (root.has("recipientId") ? root.get("recipientId").asText() : "user@company.com");
            String channelStr = root.has("channel") ? root.get("channel").asText() : "EMAIL";
            String subject = root.has("subject") ? root.get("subject").asText() :
                            (root.has("eventType") ? "TESP Event: " + root.get("eventType").asText() : "TESP Notification");
            String content = root.has("content") ? root.get("content").asText() :
                            (root.has("message") ? root.get("message").asText() : message);

            NotificationChannel channel = parseChannel(channelStr);

            NotificationRequestDTO request = NotificationRequestDTO.builder()
                    .projectId(projectId)
                    .recipient(recipient)
                    .channel(channel)
                    .subject(subject)
                    .content(content)
                    .build();

            dispatcherService.dispatchNotification(request);
        } catch (Exception e) {
            log.error("Failed to parse or process notification event payload: {}", e.getMessage(), e);
        }
    }

    private NotificationChannel parseChannel(String channelStr) {
        try {
            return NotificationChannel.valueOf(channelStr.toUpperCase());
        } catch (Exception e) {
            return NotificationChannel.EMAIL;
        }
    }
}
