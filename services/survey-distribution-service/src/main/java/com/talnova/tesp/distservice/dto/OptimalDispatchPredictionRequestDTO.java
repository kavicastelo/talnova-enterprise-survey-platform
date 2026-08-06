package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for AI optimal dispatch hour prediction")
public class OptimalDispatchPredictionRequestDTO {

    @NotBlank(message = "projectId is mandatory")
    private String projectId;

    @NotBlank(message = "employeeId is mandatory")
    private String employeeId;

    private String department;
    private String timezone;
    private DistributionChannel preferredChannel;

    public OptimalDispatchPredictionRequestDTO() {
    }

    public OptimalDispatchPredictionRequestDTO(String projectId, String employeeId, String department, String timezone, DistributionChannel preferredChannel) {
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.department = department;
        this.timezone = timezone != null ? timezone : "Asia/Colombo";
        this.preferredChannel = preferredChannel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public DistributionChannel getPreferredChannel() { return preferredChannel; }
    public void setPreferredChannel(DistributionChannel preferredChannel) { this.preferredChannel = preferredChannel; }

    public static class Builder {
        private String projectId;
        private String employeeId;
        private String department;
        private String timezone = "Asia/Colombo";
        private DistributionChannel preferredChannel;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder timezone(String timezone) { this.timezone = timezone; return this; }
        public Builder preferredChannel(DistributionChannel preferredChannel) { this.preferredChannel = preferredChannel; return this; }

        public OptimalDispatchPredictionRequestDTO build() {
            return new OptimalDispatchPredictionRequestDTO(projectId, employeeId, department, timezone, preferredChannel);
        }
    }
}
