package com.talnova.tesp.notificationservice.domain.model;

import java.time.Instant;

public class NotificationRecord {

    private String notificationId;
    private String projectId;
    private String recipient;
    private NotificationChannel channel;
    private String subject;
    private String content;
    private NotificationStatus status;
    private Instant sentAt;
    private String errorMessage;

    public NotificationRecord() {}

    public NotificationRecord(String notificationId, String projectId, String recipient, NotificationChannel channel, String subject, String content, NotificationStatus status, Instant sentAt, String errorMessage) {
        this.notificationId = notificationId;
        this.projectId = projectId;
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.content = content;
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

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

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
        private String subject;
        private String content;
        private NotificationStatus status;
        private Instant sentAt;
        private String errorMessage;

        public Builder notificationId(String notificationId) { this.notificationId = notificationId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder recipient(String recipient) { this.recipient = recipient; return this; }
        public Builder channel(NotificationChannel channel) { this.channel = channel; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder status(NotificationStatus status) { this.status = status; return this; }
        public Builder sentAt(Instant sentAt) { this.sentAt = sentAt; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }

        public NotificationRecord build() {
            return new NotificationRecord(notificationId, projectId, recipient, channel, subject, content, status, sentAt, errorMessage);
        }
    }
}
