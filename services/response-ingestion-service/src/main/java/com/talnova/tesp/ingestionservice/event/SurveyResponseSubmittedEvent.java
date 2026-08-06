package com.talnova.tesp.ingestionservice.event;

import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Kafka domain event published upon survey response intake")
public class SurveyResponseSubmittedEvent {

    private String eventId;
    private String eventType = "SURVEY_RESPONSE_SUBMITTED";
    private String projectId;
    private String campaignId;
    private String surveyId;
    private String responseId;
    private RespondentType respondentType;
    private String nodeId;
    private int answerCount;
    private Instant timestamp;

    public SurveyResponseSubmittedEvent() {
        this.timestamp = Instant.now();
    }

    public SurveyResponseSubmittedEvent(String eventId, String eventType, String projectId, String campaignId, String surveyId, String responseId, RespondentType respondentType, String nodeId, int answerCount, Instant timestamp) {
        this.eventId = eventId;
        this.eventType = eventType != null ? eventType : "SURVEY_RESPONSE_SUBMITTED";
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.surveyId = surveyId;
        this.responseId = responseId;
        this.respondentType = respondentType;
        this.nodeId = nodeId;
        this.answerCount = answerCount;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
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

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }

    public RespondentType getRespondentType() { return respondentType; }
    public void setRespondentType(RespondentType respondentType) { this.respondentType = respondentType; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public int getAnswerCount() { return answerCount; }
    public void setAnswerCount(int answerCount) { this.answerCount = answerCount; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String eventId;
        private String eventType = "SURVEY_RESPONSE_SUBMITTED";
        private String projectId;
        private String campaignId;
        private String surveyId;
        private String responseId;
        private RespondentType respondentType;
        private String nodeId;
        private int answerCount;
        private Instant timestamp = Instant.now();

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder eventType(String eventType) { this.eventType = eventType; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder responseId(String responseId) { this.responseId = responseId; return this; }
        public Builder respondentType(RespondentType respondentType) { this.respondentType = respondentType; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder answerCount(int answerCount) { this.answerCount = answerCount; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public SurveyResponseSubmittedEvent build() {
            return new SurveyResponseSubmittedEvent(eventId, eventType, projectId, campaignId, surveyId, responseId, respondentType, nodeId, answerCount, timestamp);
        }
    }
}
