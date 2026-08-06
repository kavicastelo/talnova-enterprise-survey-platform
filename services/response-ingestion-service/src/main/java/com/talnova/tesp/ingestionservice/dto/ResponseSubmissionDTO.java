package com.talnova.tesp.ingestionservice.dto;

import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Request payload for survey response submission ingestion")
public class ResponseSubmissionDTO {

    @NotBlank(message = "projectId is mandatory")
    private String projectId;

    @NotBlank(message = "campaignId is mandatory")
    private String campaignId;

    @NotBlank(message = "surveyId is mandatory")
    private String surveyId;

    private int surveyVersion = 1;

    private String responseToken;

    @NotNull(message = "respondentType is mandatory")
    private RespondentType respondentType;

    private String nodeId;

    @NotEmpty(message = "answers list cannot be empty")
    @Valid
    private List<AnswerSubmissionDTO> answers;

    public ResponseSubmissionDTO() {
    }

    public ResponseSubmissionDTO(String projectId, String campaignId, String surveyId, int surveyVersion, String responseToken, RespondentType respondentType, String nodeId, List<AnswerSubmissionDTO> answers) {
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.surveyId = surveyId;
        this.surveyVersion = surveyVersion;
        this.responseToken = responseToken;
        this.respondentType = respondentType;
        this.nodeId = nodeId;
        this.answers = answers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public int getSurveyVersion() { return surveyVersion; }
    public void setSurveyVersion(int surveyVersion) { this.surveyVersion = surveyVersion; }

    public String getResponseToken() { return responseToken; }
    public void setResponseToken(String responseToken) { this.responseToken = responseToken; }

    public RespondentType getRespondentType() { return respondentType; }
    public void setRespondentType(RespondentType respondentType) { this.respondentType = respondentType; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public List<AnswerSubmissionDTO> getAnswers() { return answers; }
    public void setAnswers(List<AnswerSubmissionDTO> answers) { this.answers = answers; }

    public static class Builder {
        private String projectId;
        private String campaignId;
        private String surveyId;
        private int surveyVersion = 1;
        private String responseToken;
        private RespondentType respondentType;
        private String nodeId;
        private List<AnswerSubmissionDTO> answers;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder surveyVersion(int surveyVersion) { this.surveyVersion = surveyVersion; return this; }
        public Builder responseToken(String responseToken) { this.responseToken = responseToken; return this; }
        public Builder respondentType(RespondentType respondentType) { this.respondentType = respondentType; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder answers(List<AnswerSubmissionDTO> answers) { this.answers = answers; return this; }

        public ResponseSubmissionDTO build() {
            return new ResponseSubmissionDTO(projectId, campaignId, surveyId, surveyVersion, responseToken, respondentType, nodeId, answers);
        }
    }
}
