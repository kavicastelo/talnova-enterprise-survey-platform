package com.talnova.tesp.notificationservice.dto;

import com.talnova.tesp.notificationservice.domain.model.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequestDTO {

    private String projectId;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @NotNull(message = "Notification channel is required")
    private NotificationChannel channel;

    private String subject;

    @NotBlank(message = "Message content is required")
    private String content;

    public NotificationRequestDTO() {}

    public NotificationRequestDTO(String projectId, String recipient, NotificationChannel channel, String subject, String content) {
        this.projectId = projectId;
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.content = content;
    }

    public static Builder builder() { return new Builder(); }

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

    public static class Builder {
        private String projectId;
        private String recipient;
        private NotificationChannel channel;
        private String subject;
        private String content;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder recipient(String recipient) { this.recipient = recipient; return this; }
        public Builder channel(NotificationChannel channel) { this.channel = channel; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder content(String content) { this.content = content; return this; }

        public NotificationRequestDTO build() {
            return new NotificationRequestDTO(projectId, recipient, channel, subject, content);
        }
    }
}
