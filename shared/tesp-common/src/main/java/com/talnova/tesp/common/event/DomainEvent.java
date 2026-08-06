package com.talnova.tesp.common.event;

import java.time.Instant;

public class DomainEvent<T> {

    private String eventId;
    private String eventType;
    private String projectId;
    private String correlationId;
    private Instant timestamp;
    private T payload;

    public DomainEvent() {
        this.timestamp = Instant.now();
    }

    public DomainEvent(String eventId, String eventType, String projectId, String correlationId, T payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.projectId = projectId;
        this.correlationId = correlationId;
        this.timestamp = Instant.now();
        this.payload = payload;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
