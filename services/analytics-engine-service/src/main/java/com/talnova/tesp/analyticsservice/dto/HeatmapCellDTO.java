package com.talnova.tesp.analyticsservice.dto;

public class HeatmapCellDTO {

    private String nodeId;
    private String groupId;
    private int sampleSize;
    private Double score;
    private String colorIntensity; // RED (< 50%), YELLOW (50-70%), GREEN (> 70%), GREY (suppressed)

    public HeatmapCellDTO() {}

    public HeatmapCellDTO(String nodeId, String groupId, int sampleSize, Double score, String colorIntensity) {
        this.nodeId = nodeId;
        this.groupId = groupId;
        this.sampleSize = sampleSize;
        this.score = score;
        this.colorIntensity = colorIntensity;
    }

    public static Builder builder() { return new Builder(); }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public int getSampleSize() { return sampleSize; }
    public void setSampleSize(int sampleSize) { this.sampleSize = sampleSize; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getColorIntensity() { return colorIntensity; }
    public void setColorIntensity(String colorIntensity) { this.colorIntensity = colorIntensity; }

    public static class Builder {
        private String nodeId;
        private String groupId;
        private int sampleSize;
        private Double score;
        private String colorIntensity;

        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder groupId(String groupId) { this.groupId = groupId; return this; }
        public Builder sampleSize(int sampleSize) { this.sampleSize = sampleSize; return this; }
        public Builder score(Double score) { this.score = score; return this; }
        public Builder colorIntensity(String colorIntensity) { this.colorIntensity = colorIntensity; return this; }

        public HeatmapCellDTO build() {
            return new HeatmapCellDTO(nodeId, groupId, sampleSize, score, colorIntensity);
        }
    }
}
