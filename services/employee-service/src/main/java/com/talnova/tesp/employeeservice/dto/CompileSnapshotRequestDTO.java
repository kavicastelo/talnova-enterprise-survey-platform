package com.talnova.tesp.employeeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "Compile Demographic Snapshot Request Payload")
public class CompileSnapshotRequestDTO {

    @NotBlank(message = "projectId is required")
    private String projectId;

    @NotBlank(message = "surveyId is required")
    private String surveyId;

    private List<String> employeeIds;

    public CompileSnapshotRequestDTO() {
    }

    public CompileSnapshotRequestDTO(String projectId, String surveyId, List<String> employeeIds) {
        this.projectId = projectId;
        this.surveyId = surveyId;
        this.employeeIds = employeeIds;
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public List<String> getEmployeeIds() { return employeeIds; }
    public void setEmployeeIds(List<String> employeeIds) { this.employeeIds = employeeIds; }
}
