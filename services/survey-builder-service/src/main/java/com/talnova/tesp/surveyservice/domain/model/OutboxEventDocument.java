package com.talnova.tesp.surveyservice.domain.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB Document entity representing transactional outbox event messages for reliable Kafka streaming.
 */
@Document(collection = "outbox_events")
public class OutboxEventDocument {

    @Id
    private String id;
    private String eventId;
    private String eventType;
    private String projectId;
    private String payload;
    private OutboxStatus status;
    private int retryCount;

    @CreatedDate
    private Instant createdAt;
    private Instant processedAt;

    public OutboxEventDocument() {
    }

    public OutboxEventDocument(String id, String eventId, String eventType, String projectId, String payload,
                               OutboxStatus status, int retryCount, Instant createdAt, Instant processedAt) {
        this.id = id;
        this.eventId = eventId;
        this.eventType = eventType;
        this.projectId = projectId;
        this.payload = payload;
        this.status = status;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public OutboxStatus getStatus() { return status; }
    public void setStatus(OutboxStatus status) { this.status = status; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }

    public static class Builder {
        private String id;
        private String eventId;
        private String eventType;
        private String projectId;
        private String payload;
        private OutboxStatus status;
        private int retryCount;
        private Instant createdAt;
        private Instant processedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder eventType(String eventType) { this.eventType = eventType; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder payload(String payload) { this.payload = payload; return this; }
        public Builder status(OutboxStatus status) { this.status = status; return this; }
        public Builder retryCount(int retryCount) { this.retryCount = retryCount; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder processedAt(Instant processedAt) { this.processedAt = processedAt; return this; }

        public OutboxEventDocument build() {
            return new OutboxEventDocument(id, eventId, eventType, projectId, payload, status, retryCount, createdAt, processedAt);
        }
    }
}
