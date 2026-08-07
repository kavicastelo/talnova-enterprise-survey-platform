package com.talnova.tesp.notificationservice.dto;

import com.talnova.tesp.notificationservice.domain.model.NotificationChannel;
import com.talnova.tesp.notificationservice.domain.model.NotificationStatus;

import java.time.Instant;

public class NotificationResponseDTO {

    private String notificationId;
    private String projectId;
    private String recipient;
    private NotificationChannel channel;
    private NotificationStatus status;
    private Instant sentAt;
    private String errorMessage;

    public NotificationResponseDTO() {}

    public NotificationResponseDTO(String notificationId, String projectId, String recipient, NotificationChannel channel, NotificationStatus status, Instant sentAt, String errorMessage) {
        this.notificationId = notificationId;
        this.projectId = projectId;
        this.recipient = recipient;
        this.channel = channel;
        this.status = status;
        this.sentAt = sentAt;
        this.errorMessage = errorMessage;
    }

    public static Builder builder() { return new Builder(); }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public static class Builder {
        private String notificationId;
        private String projectId;
        private String recipient;
        private NotificationChannel channel;
        private NotificationStatus status;
        private Instant sentAt;
        private String errorMessage;

        public Builder notificationId(String notificationId) { this.notificationId = notificationId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder recipient(String recipient) { this.recipient = recipient; return this; }
        public Builder channel(NotificationChannel channel) { this.channel = channel; return this; }
        public Builder status(NotificationStatus status) { this.status = status; return this; }
        public Builder sentAt(Instant sentAt) { this.sentAt = sentAt; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }

        public NotificationResponseDTO build() {
            return new NotificationResponseDTO(notificationId, projectId, recipient, channel, status, sentAt, errorMessage);
        }
    }
}
