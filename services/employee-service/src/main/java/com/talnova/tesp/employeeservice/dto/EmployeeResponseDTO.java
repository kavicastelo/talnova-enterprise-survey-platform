package com.talnova.tesp.employeeservice.dto;

import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Schema(description = "Employee Profile Response Payload")
public class EmployeeResponseDTO {

    private String id;
    private String projectId;
    private String employeeId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String nodeId;
    private List<String> matrixNodeIds;
    private EmployeeStatus status;
    private Map<String, Object> attributes;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    public EmployeeResponseDTO() {
    }

    public EmployeeResponseDTO(String id, String projectId, String employeeId, String email,
                               String fullName, String phoneNumber, String nodeId,
                               List<String> matrixNodeIds, EmployeeStatus status,
                               Map<String, Object> attributes, Long version,
                               Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.nodeId = nodeId;
        this.matrixNodeIds = matrixNodeIds;
        this.status = status;
        this.attributes = attributes;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String employeeId;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String nodeId;
        private List<String> matrixNodeIds;
        private EmployeeStatus status;
        private Map<String, Object> attributes;
        private Long version;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder matrixNodeIds(List<String> matrixNodeIds) { this.matrixNodeIds = matrixNodeIds; return this; }
        public Builder status(EmployeeStatus status) { this.status = status; return this; }
        public Builder attributes(Map<String, Object> attributes) { this.attributes = attributes; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public EmployeeResponseDTO build() {
            return new EmployeeResponseDTO(id, projectId, employeeId, email, fullName, phoneNumber, nodeId, matrixNodeIds, status, attributes, version, createdAt, updatedAt);
        }
    }
}
