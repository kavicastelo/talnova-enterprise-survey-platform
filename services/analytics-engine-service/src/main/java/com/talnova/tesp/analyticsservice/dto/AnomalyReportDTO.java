package com.talnova.tesp.analyticsservice.dto;

public class AnomalyReportDTO {

    private String nodeId;
    private String nodeName;
    private String metricName;
    private Double currentScore;
    private Double baselineScore;
    private Double scoreDropDelta;
    private String severity; // CRITICAL, WARNING
    private String insightMessage;

    public AnomalyReportDTO() {}

    public AnomalyReportDTO(String nodeId, String nodeName, String metricName, Double currentScore, Double baselineScore, Double scoreDropDelta, String severity, String insightMessage) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.metricName = metricName;
        this.currentScore = currentScore;
        this.baselineScore = baselineScore;
        this.scoreDropDelta = scoreDropDelta;
        this.severity = severity;
        this.insightMessage = insightMessage;
    }

    public static Builder builder() { return new Builder(); }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }

    public Double getCurrentScore() { return currentScore; }
    public void setCurrentScore(Double currentScore) { this.currentScore = currentScore; }

    public Double getBaselineScore() { return baselineScore; }
    public void setBaselineScore(Double baselineScore) { this.baselineScore = baselineScore; }

    public Double getScoreDropDelta() { return scoreDropDelta; }
    public void setScoreDropDelta(Double scoreDropDelta) { this.scoreDropDelta = scoreDropDelta; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getInsightMessage() { return insightMessage; }
    public void setInsightMessage(String insightMessage) { this.insightMessage = insightMessage; }

    public static class Builder {
        private String nodeId;
        private String nodeName;
        private String metricName;
        private Double currentScore;
        private Double baselineScore;
        private Double scoreDropDelta;
        private String severity;
        private String insightMessage;

        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder nodeName(String nodeName) { this.nodeName = nodeName; return this; }
        public Builder metricName(String metricName) { this.metricName = metricName; return this; }
        public Builder currentScore(Double currentScore) { this.currentScore = currentScore; return this; }
        public Builder baselineScore(Double baselineScore) { this.baselineScore = baselineScore; return this; }
        public Builder scoreDropDelta(Double scoreDropDelta) { this.scoreDropDelta = scoreDropDelta; return this; }
        public Builder severity(String severity) { this.severity = severity; return this; }
        public Builder insightMessage(String insightMessage) { this.insightMessage = insightMessage; return this; }

        public AnomalyReportDTO build() {
            return new AnomalyReportDTO(nodeId, nodeName, metricName, currentScore, baselineScore, scoreDropDelta, severity, insightMessage);
        }
    }
}
