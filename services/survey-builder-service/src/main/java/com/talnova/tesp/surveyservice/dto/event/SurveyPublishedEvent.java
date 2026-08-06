package com.talnova.tesp.surveyservice.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Kafka domain event emitted when a survey AST version is published")
public class SurveyPublishedEvent {

    private String eventId;
    private String eventType;
    private String projectId;
    private String surveyId;
    private int version;
    private int questionCount;
    private Instant timestamp;

    public SurveyPublishedEvent() {
    }

    public SurveyPublishedEvent(String eventId, String eventType, String projectId, String surveyId,
                                int version, int questionCount, Instant timestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.projectId = projectId;
        this.surveyId = surveyId;
        this.version = version;
        this.questionCount = questionCount;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String eventId;
        private String eventType = "SURVEY_PUBLISHED";
        private String projectId;
        private String surveyId;
        private int version;
        private int questionCount;
        private Instant timestamp;

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder eventType(String eventType) { this.eventType = eventType; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder version(int version) { this.version = version; return this; }
        public Builder questionCount(int questionCount) { this.questionCount = questionCount; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public SurveyPublishedEvent build() {
            return new SurveyPublishedEvent(eventId, eventType, projectId, surveyId, version, questionCount, timestamp);
        }
    }
}
