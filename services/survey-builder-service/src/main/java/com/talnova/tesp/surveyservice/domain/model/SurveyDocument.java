package com.talnova.tesp.surveyservice.domain.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MongoDB Document entity representing an immutable/versioned survey AST questionnaire.
 */
@Document(collection = "surveys")
@CompoundIndex(name = "uk_surveys_project_survey_version", def = "{'projectId': 1, 'surveyId': 1, 'version': 1}", unique = true)
@CompoundIndex(name = "idx_surveys_project_status_deleted", def = "{'projectId': 1, 'status': 1, 'isDeleted': 1}")
@CompoundIndex(name = "idx_surveys_project_survey_deleted", def = "{'projectId': 1, 'surveyId': 1, 'isDeleted': 1}")
public class SurveyDocument {

    @Id
    private String id;

    private String projectId;

    private String surveyId;

    @Version
    private Integer version;

    private Map<String, String> title = new HashMap<>();

    private Map<String, String> description = new HashMap<>();

    private SurveyStatus status = SurveyStatus.DRAFT;

    private List<SurveyPage> pages = new ArrayList<>();

    private Instant publishedAt;

    private List<Integer> versionHistory = new ArrayList<>();

    private boolean isDeleted;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public SurveyDocument() {
    }

    public SurveyDocument(String id, String projectId, String surveyId, Integer version, Map<String, String> title,
                          Map<String, String> description, SurveyStatus status, List<SurveyPage> pages,
                          Instant publishedAt, List<Integer> versionHistory, boolean isDeleted,
                          Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.surveyId = surveyId;
        this.version = version;
        this.title = title != null ? title : new HashMap<>();
        this.description = description != null ? description : new HashMap<>();
        this.status = status != null ? status : SurveyStatus.DRAFT;
        this.pages = pages != null ? pages : new ArrayList<>();
        this.publishedAt = publishedAt;
        this.versionHistory = versionHistory != null ? versionHistory : new ArrayList<>();
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public SurveyStatus getStatus() {
        return status;
    }

    public void setStatus(SurveyStatus status) {
        this.status = status;
    }

    public List<SurveyPage> getPages() {
        return pages;
    }

    public void setPages(List<SurveyPage> pages) {
        this.pages = pages;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<Integer> getVersionHistory() {
        return versionHistory;
    }

    public void setVersionHistory(List<Integer> versionHistory) {
        this.versionHistory = versionHistory;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class Builder {
        private String id;
        private String projectId;
        private String surveyId;
        private Integer version;
        private Map<String, String> title = new HashMap<>();
        private Map<String, String> description = new HashMap<>();
        private SurveyStatus status = SurveyStatus.DRAFT;
        private List<SurveyPage> pages = new ArrayList<>();
        private Instant publishedAt;
        private List<Integer> versionHistory = new ArrayList<>();
        private boolean isDeleted;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder version(Integer version) { this.version = version; return this; }
        public Builder title(Map<String, String> title) { this.title = title; return this; }
        public Builder description(Map<String, String> description) { this.description = description; return this; }
        public Builder status(SurveyStatus status) { this.status = status; return this; }
        public Builder pages(List<SurveyPage> pages) { this.pages = pages; return this; }
        public Builder publishedAt(Instant publishedAt) { this.publishedAt = publishedAt; return this; }
        public Builder versionHistory(List<Integer> versionHistory) { this.versionHistory = versionHistory; return this; }
        public Builder isDeleted(boolean isDeleted) { this.isDeleted = isDeleted; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public SurveyDocument build() {
            return new SurveyDocument(id, projectId, surveyId, version, title, description, status, pages, publishedAt, versionHistory, isDeleted, createdAt, updatedAt);
        }
    }
}
