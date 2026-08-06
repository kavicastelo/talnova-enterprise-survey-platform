package com.talnova.tesp.orgservice.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "outbox_events")
public class OutboxEventDocument {

    @Id
    private String id;

    private String eventId;
    private String projectId;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String payload;

    @Indexed
    private OutboxStatus status;

    @CreatedDate
    private Instant createdAt;

    private Instant processedAt;

    public OutboxEventDocument() {
    }

    public OutboxEventDocument(String id, String eventId, String projectId, String aggregateType,
                               String aggregateId, String eventType, String payload,
                               OutboxStatus status, Instant createdAt, Instant processedAt) {
        this.id = id;
        this.eventId = eventId;
        this.projectId = projectId;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
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

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getAggregateType() { return aggregateType; }
    public void setAggregateType(String aggregateType) { this.aggregateType = aggregateType; }

    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public OutboxStatus getStatus() { return status; }
    public void setStatus(OutboxStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }

    public static class Builder {
        private String id;
        private String eventId;
        private String projectId;
        private String aggregateType;
        private String aggregateId;
        private String eventType;
        private String payload;
        private OutboxStatus status;
        private Instant createdAt;
        private Instant processedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder aggregateType(String aggregateType) { this.aggregateType = aggregateType; return this; }
        public Builder aggregateId(String aggregateId) { this.aggregateId = aggregateId; return this; }
        public Builder eventType(String eventType) { this.eventType = eventType; return this; }
        public Builder payload(String payload) { this.payload = payload; return this; }
        public Builder status(OutboxStatus status) { this.status = status; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder processedAt(Instant processedAt) { this.processedAt = processedAt; return this; }

        public OutboxEventDocument build() {
            return new OutboxEventDocument(id, eventId, projectId, aggregateType, aggregateId, eventType, payload, status, createdAt, processedAt);
        }
    }
}
