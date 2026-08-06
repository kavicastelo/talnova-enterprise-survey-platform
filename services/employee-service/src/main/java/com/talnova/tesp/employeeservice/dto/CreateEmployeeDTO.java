package com.talnova.tesp.employeeservice.dto;

import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Map;

@Schema(description = "Create Employee Profile Payload")
public class CreateEmployeeDTO {

    @NotBlank(message = "projectId is required")
    @Schema(description = "Project workspace identifier", example = "PRJ-99201")
    private String projectId;

    @NotBlank(message = "employeeId is required")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,30}$", message = "employeeId must match pattern ^[A-Za-z0-9_-]{2,30}$")
    @Schema(description = "Unique employee identifier within project workspace", example = "EMP-10020")
    private String employeeId;

    @Email(message = "email must be a valid RFC 5322 format")
    @Schema(description = "Sensitive PII email address (CSFLE encrypted)", example = "john.doe@aitkenspence.lk")
    private String email;

    @NotBlank(message = "fullName is required")
    @Schema(description = "Sensitive PII full legal name (CSFLE encrypted)", example = "John Doe")
    private String fullName;

    @Schema(description = "Sensitive PII phone number (CSFLE encrypted)", example = "+94771234567")
    private String phoneNumber;

    @NotBlank(message = "nodeId is required")
    @Schema(description = "Primary organizational node assignment identifier from FEAT-002", example = "N-201")
    private String nodeId;

    @Schema(description = "Optional array of secondary matrix organizational node IDs", example = "[\"N-301\", \"N-401\"]")
    private List<String> matrixNodeIds;

    @Schema(description = "Employment lifecycle status", example = "ACTIVE")
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @Schema(description = "Dynamic key-value demographic attribute map")
    private Map<String, Object> attributes;

    public CreateEmployeeDTO() {
    }

    public CreateEmployeeDTO(String projectId, String employeeId, String email, String fullName,
                             String phoneNumber, String nodeId, List<String> matrixNodeIds,
                             EmployeeStatus status, Map<String, Object> attributes) {
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.nodeId = nodeId;
        this.matrixNodeIds = matrixNodeIds;
        this.status = status != null ? status : EmployeeStatus.ACTIVE;
        this.attributes = attributes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public List<String> getMatrixNodeIds() { return matrixNodeIds; }
    public void setMatrixNodeIds(List<String> matrixNodeIds) { this.matrixNodeIds = matrixNodeIds; }

    public EmployeeStatus getStatus() { return status; }
    public void setStatus(EmployeeStatus status) { this.status = status; }

    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }

    public static class Builder {
        private String projectId;
        private String employeeId;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String nodeId;
        private List<String> matrixNodeIds;
        private EmployeeStatus status = EmployeeStatus.ACTIVE;
        private Map<String, Object> attributes;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder matrixNodeIds(List<String> matrixNodeIds) { this.matrixNodeIds = matrixNodeIds; return this; }
        public Builder status(EmployeeStatus status) { this.status = status; return this; }
        public Builder attributes(Map<String, Object> attributes) { this.attributes = attributes; return this; }

        public CreateEmployeeDTO build() {
            return new CreateEmployeeDTO(projectId, employeeId, email, fullName, phoneNumber, nodeId, matrixNodeIds, status, attributes);
        }
    }
}
