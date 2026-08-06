package com.talnova.tesp.employeeservice.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "employee_snapshots")
@CompoundIndexes({
        @CompoundIndex(name = "uniq_survey_emp_snapshot", def = "{'surveyId': 1, 'employeeId': 1}", unique = true),
        @CompoundIndex(name = "idx_proj_survey_snapshot", def = "{'projectId': 1, 'surveyId': 1}")
})
public class DemographicSnapshotDocument {

    @Id
    private String id;

    private String projectId;
    private String surveyId;
    private String employeeId;
    private String nodeId;
    private List<String> matrixNodeIds;
    private Map<String, Object> demographics;

    @CreatedDate
    private Instant capturedAt;

    public DemographicSnapshotDocument() {
    }

    public DemographicSnapshotDocument(String id, String projectId, String surveyId, String employeeId,
                                       String nodeId, List<String> matrixNodeIds,
                                       Map<String, Object> demographics, Instant capturedAt) {
        this.id = id;
        this.projectId = projectId;
        this.surveyId = surveyId;
        this.employeeId = employeeId;
        this.nodeId = nodeId;
        this.matrixNodeIds = matrixNodeIds;
        this.demographics = demographics;
        this.capturedAt = capturedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public List<String> getMatrixNodeIds() { return matrixNodeIds; }
    public void setMatrixNodeIds(List<String> matrixNodeIds) { this.matrixNodeIds = matrixNodeIds; }

    public Map<String, Object> getDemographics() { return demographics; }
    public void setDemographics(Map<String, Object> demographics) { this.demographics = demographics; }

    public Instant getCapturedAt() { return capturedAt; }
    public void setCapturedAt(Instant capturedAt) { this.capturedAt = capturedAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String surveyId;
        private String employeeId;
        private String nodeId;
        private List<String> matrixNodeIds;
        private Map<String, Object> demographics;
        private Instant capturedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder matrixNodeIds(List<String> matrixNodeIds) { this.matrixNodeIds = matrixNodeIds; return this; }
        public Builder demographics(Map<String, Object> demographics) { this.demographics = demographics; return this; }
        public Builder capturedAt(Instant capturedAt) { this.capturedAt = capturedAt; return this; }

        public DemographicSnapshotDocument build() {
            return new DemographicSnapshotDocument(id, projectId, surveyId, employeeId, nodeId, matrixNodeIds, demographics, capturedAt);
        }
    }
}
