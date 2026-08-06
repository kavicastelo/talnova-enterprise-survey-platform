package com.talnova.tesp.orgservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "AI Hierarchy Anomaly Inspection Report")
public class HierarchyAnomalyReportDTO {

    private String projectId;
    private int totalNodesInspected;
    private int totalAnomaliesDetected;
    private List<AnomalyDetail> anomalies;

    public HierarchyAnomalyReportDTO() {
    }

    public HierarchyAnomalyReportDTO(String projectId, int totalNodesInspected, int totalAnomaliesDetected, List<AnomalyDetail> anomalies) {
        this.projectId = projectId;
        this.totalNodesInspected = totalNodesInspected;
        this.totalAnomaliesDetected = totalAnomaliesDetected;
        this.anomalies = anomalies;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public int getTotalNodesInspected() { return totalNodesInspected; }
    public void setTotalNodesInspected(int totalNodesInspected) { this.totalNodesInspected = totalNodesInspected; }

    public int getTotalAnomaliesDetected() { return totalAnomaliesDetected; }
    public void setTotalAnomaliesDetected(int totalAnomaliesDetected) { this.totalAnomaliesDetected = totalAnomaliesDetected; }

    public List<AnomalyDetail> getAnomalies() { return anomalies; }
    public void setAnomalies(List<AnomalyDetail> anomalies) { this.anomalies = anomalies; }

    @Schema(description = "Individual Hierarchy Anomaly Detail")
    public static class AnomalyDetail {
        private String anomalyType;
        private String nodeId;
        private String severity;
        private String message;
        private String recommendation;

        public AnomalyDetail() {
        }

        public AnomalyDetail(String anomalyType, String nodeId, String severity, String message, String recommendation) {
            this.anomalyType = anomalyType;
            this.nodeId = nodeId;
            this.severity = severity;
            this.message = message;
            this.recommendation = recommendation;
        }

        public static AnomalyDetailBuilder builder() {
            return new AnomalyDetailBuilder();
        }

        public String getAnomalyType() { return anomalyType; }
        public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }

        public String getNodeId() { return nodeId; }
        public void setNodeId(String nodeId) { this.nodeId = nodeId; }

        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

        public static class AnomalyDetailBuilder {
            private String anomalyType;
            private String nodeId;
            private String severity;
            private String message;
            private String recommendation;

            public AnomalyDetailBuilder anomalyType(String anomalyType) { this.anomalyType = anomalyType; return this; }
            public AnomalyDetailBuilder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
            public AnomalyDetailBuilder severity(String severity) { this.severity = severity; return this; }
            public AnomalyDetailBuilder message(String message) { this.message = message; return this; }
            public AnomalyDetailBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }

            public AnomalyDetail build() {
                return new AnomalyDetail(anomalyType, nodeId, severity, message, recommendation);
            }
        }
    }

    public static class Builder {
        private String projectId;
        private int totalNodesInspected;
        private int totalAnomaliesDetected;
        private List<AnomalyDetail> anomalies;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder totalNodesInspected(int totalNodesInspected) { this.totalNodesInspected = totalNodesInspected; return this; }
        public Builder totalAnomaliesDetected(int totalAnomaliesDetected) { this.totalAnomaliesDetected = totalAnomaliesDetected; return this; }
        public Builder anomalies(List<AnomalyDetail> anomalies) { this.anomalies = anomalies; return this; }

        public HierarchyAnomalyReportDTO build() {
            return new HierarchyAnomalyReportDTO(projectId, totalNodesInspected, totalAnomaliesDetected, anomalies);
        }
    }
}
