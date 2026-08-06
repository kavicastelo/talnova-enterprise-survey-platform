package com.talnova.tesp.ingestionservice.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "survey_responses")
@CompoundIndexes({
        @CompoundIndex(name = "idx_projectId_campaignId", def = "{'projectId': 1, 'campaignId': 1}"),
        @CompoundIndex(name = "idx_responseToken", def = "{'responseToken': 1}"),
        @CompoundIndex(name = "idx_projectId_campaignId_nodeId", def = "{'projectId': 1, 'campaignId': 1, 'nodeId': 1}")
})
public class SurveyResponseDocument {

    @Id
    private String id;

    private String projectId;
    private String campaignId;
    private String surveyId;
    private int surveyVersion;
    private RespondentType respondentType;
    private String responseToken;
    private String nodeId;
    private Map<String, String> demographicSnapshot;
    private List<AnswerItem> answers;
    private Instant submittedAt;
    private boolean isDeleted;

    public SurveyResponseDocument() {
        this.demographicSnapshot = new HashMap<>();
        this.answers = new ArrayList<>();
        this.submittedAt = Instant.now();
        this.isDeleted = false;
    }

    public SurveyResponseDocument(String id, String projectId, String campaignId, String surveyId, int surveyVersion,
                                  RespondentType respondentType, String responseToken, String nodeId,
                                  Map<String, String> demographicSnapshot, List<AnswerItem> answers,
                                  Instant submittedAt, boolean isDeleted) {
        this.id = id;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.surveyId = surveyId;
        this.surveyVersion = surveyVersion;
        this.respondentType = respondentType;
        this.responseToken = responseToken;
        this.nodeId = nodeId;
        this.demographicSnapshot = demographicSnapshot != null ? demographicSnapshot : new HashMap<>();
        this.answers = answers != null ? answers : new ArrayList<>();
        this.submittedAt = submittedAt != null ? submittedAt : Instant.now();
        this.isDeleted = isDeleted;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public int getSurveyVersion() { return surveyVersion; }
    public void setSurveyVersion(int surveyVersion) { this.surveyVersion = surveyVersion; }

    public RespondentType getRespondentType() { return respondentType; }
    public void setRespondentType(RespondentType respondentType) { this.respondentType = respondentType; }

    public String getResponseToken() { return responseToken; }
    public void setResponseToken(String responseToken) { this.responseToken = responseToken; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public Map<String, String> getDemographicSnapshot() { return demographicSnapshot; }
    public void setDemographicSnapshot(Map<String, String> demographicSnapshot) { this.demographicSnapshot = demographicSnapshot; }

    public List<AnswerItem> getAnswers() { return answers; }
    public void setAnswers(List<AnswerItem> answers) { this.answers = answers; }

    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public static class Builder {
        private String id;
        private String projectId;
        private String campaignId;
        private String surveyId;
        private int surveyVersion;
        private RespondentType respondentType;
        private String responseToken;
        private String nodeId;
        private Map<String, String> demographicSnapshot = new HashMap<>();
        private List<AnswerItem> answers = new ArrayList<>();
        private Instant submittedAt = Instant.now();
        private boolean isDeleted = false;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder surveyVersion(int surveyVersion) { this.surveyVersion = surveyVersion; return this; }
        public Builder respondentType(RespondentType respondentType) { this.respondentType = respondentType; return this; }
        public Builder responseToken(String responseToken) { this.responseToken = responseToken; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder demographicSnapshot(Map<String, String> demographicSnapshot) { this.demographicSnapshot = demographicSnapshot; return this; }
        public Builder answers(List<AnswerItem> answers) { this.answers = answers; return this; }
        public Builder submittedAt(Instant submittedAt) { this.submittedAt = submittedAt; return this; }
        public Builder isDeleted(boolean isDeleted) { this.isDeleted = isDeleted; return this; }

        public SurveyResponseDocument build() {
            return new SurveyResponseDocument(id, projectId, campaignId, surveyId, surveyVersion, respondentType, responseToken, nodeId, demographicSnapshot, answers, submittedAt, isDeleted);
        }
    }
}
