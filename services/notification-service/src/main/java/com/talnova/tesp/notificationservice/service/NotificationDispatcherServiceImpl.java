package com.talnova.tesp.notificationservice.service;

import com.talnova.tesp.notificationservice.domain.model.NotificationChannel;
import com.talnova.tesp.notificationservice.domain.model.NotificationRecord;
import com.talnova.tesp.notificationservice.domain.model.NotificationStatus;
import com.talnova.tesp.notificationservice.dto.NotificationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationDispatcherServiceImpl implements NotificationDispatcherService {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcherServiceImpl.class);

    private final Map<String, NotificationRecord> notificationStore = new ConcurrentHashMap<>();

    @Override
    public NotificationRecord dispatchNotification(NotificationRequestDTO request) {
        String notificationId = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Dispatching notification '{}' via channel '{}' to recipient '{}'",
                notificationId, request.getChannel(), request.getRecipient());

        boolean success = sendToChannel(request.getChannel(), request.getRecipient(), request.getSubject(), request.getContent());

        NotificationRecord record = NotificationRecord.builder()
                .notificationId(notificationId)
                .projectId(request.getProjectId())
                .recipient(request.getRecipient())
                .channel(request.getChannel())
                .subject(request.getSubject())
                .content(request.getContent())
                .status(success ? NotificationStatus.DELIVERED : NotificationStatus.FAILED)
                .sentAt(Instant.now())
                .errorMessage(success ? null : "Failed to deliver to channel " + request.getChannel())
                .build();

        notificationStore.put(notificationId, record);
        log.info("Notification '{}' dispatch completed with status '{}'", notificationId, record.getStatus());
        return record;
    }

    @Override
    public Optional<NotificationRecord> getNotificationStatus(String notificationId) {
        return Optional.ofNullable(notificationStore.get(notificationId));
    }

    private boolean sendToChannel(NotificationChannel channel, String recipient, String subject, String content) {
        switch (channel) {
            case EMAIL:
                log.info("SMTP/SES Email Adapter: Sending email to '{}' (Subject: '{}')", recipient, subject);
                return true;
            case SMS:
                log.info("Twilio SMS Adapter: Sending SMS to '{}'", recipient);
                return true;
            case MS_TEAMS:
                log.info("MS Teams Webhook Adapter: Posting message to webhook recipient '{}'", recipient);
                return true;
            case SLACK:
                log.info("Slack Webhook Adapter: Posting message to channel/webhook recipient '{}'", recipient);
                return true;
            case KIOSK_PIN:
                log.info("Kiosk PIN Adapter: Generating PIN notification for recipient '{}'", recipient);
                return true;
            default:
                log.warn("Unsupported notification channel: '{}'", channel);
                return false;
        }
    }
}
