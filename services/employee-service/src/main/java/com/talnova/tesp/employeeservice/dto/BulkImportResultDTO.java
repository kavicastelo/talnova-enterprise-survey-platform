package com.talnova.tesp.employeeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Bulk Ingestion Summary Report DTO")
public class BulkImportResultDTO {

    private String jobId;
    private String projectId;
    private int totalProcessed;
    private int insertedCount;
    private int updatedCount;
    private int terminatedCount;
    private int failedCount;
    private List<String> errors = new ArrayList<>();

    public BulkImportResultDTO() {
    }

    public BulkImportResultDTO(String jobId, String projectId, int totalProcessed, int insertedCount,
                               int updatedCount, int terminatedCount, int failedCount, List<String> errors) {
        this.jobId = jobId;
        this.projectId = projectId;
        this.totalProcessed = totalProcessed;
        this.insertedCount = insertedCount;
        this.updatedCount = updatedCount;
        this.terminatedCount = terminatedCount;
        this.failedCount = failedCount;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public int getTotalProcessed() { return totalProcessed; }
    public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }

    public int getInsertedCount() { return insertedCount; }
    public void setInsertedCount(int insertedCount) { this.insertedCount = insertedCount; }

    public int getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(int updatedCount) { this.updatedCount = updatedCount; }

    public int getTerminatedCount() { return terminatedCount; }
    public void setTerminatedCount(int terminatedCount) { this.terminatedCount = terminatedCount; }

    public int getFailedCount() { return failedCount; }
    public void setFailedCount(int failedCount) { this.failedCount = failedCount; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public static class Builder {
        private String jobId;
        private String projectId;
        private int totalProcessed;
        private int insertedCount;
        private int updatedCount;
        private int terminatedCount;
        private int failedCount;
        private List<String> errors = new ArrayList<>();

        public Builder jobId(String jobId) { this.jobId = jobId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder totalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; return this; }
        public Builder insertedCount(int insertedCount) { this.insertedCount = insertedCount; return this; }
        public Builder updatedCount(int updatedCount) { this.updatedCount = updatedCount; return this; }
        public Builder terminatedCount(int terminatedCount) { this.terminatedCount = terminatedCount; return this; }
        public Builder failedCount(int failedCount) { this.failedCount = failedCount; return this; }
        public Builder errors(List<String> errors) { this.errors = errors; return this; }

        public BulkImportResultDTO build() {
            return new BulkImportResultDTO(jobId, projectId, totalProcessed, insertedCount, updatedCount, terminatedCount, failedCount, errors);
        }
    }
}
