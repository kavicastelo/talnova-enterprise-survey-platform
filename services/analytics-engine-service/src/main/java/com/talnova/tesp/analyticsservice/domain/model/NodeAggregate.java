package com.talnova.tesp.analyticsservice.domain.model;

public class NodeAggregate {

    private String nodeId;
    private String nodePath;
    private Integer responseCount;
    private NodeAggregateStatus status;
    private Double enps;
    private Double engagementIndex;

    public NodeAggregate() {}

    public NodeAggregate(String nodeId, String nodePath, Integer responseCount, NodeAggregateStatus status, Double enps, Double engagementIndex) {
        this.nodeId = nodeId;
        this.nodePath = nodePath;
        this.responseCount = responseCount;
        this.status = status;
        this.enps = enps;
        this.engagementIndex = engagementIndex;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getNodePath() { return nodePath; }
    public void setNodePath(String nodePath) { this.nodePath = nodePath; }

    public Integer getResponseCount() { return responseCount; }
    public void setResponseCount(Integer responseCount) { this.responseCount = responseCount; }

    public NodeAggregateStatus getStatus() { return status; }
    public void setStatus(NodeAggregateStatus status) { this.status = status; }

    public Double getEnps() { return enps; }
    public void setEnps(Double enps) { this.enps = enps; }

    public Double getEngagementIndex() { return engagementIndex; }
    public void setEngagementIndex(Double engagementIndex) { this.engagementIndex = engagementIndex; }

    public static class Builder {
        private String nodeId;
        private String nodePath;
        private Integer responseCount;
        private NodeAggregateStatus status;
        private Double enps;
        private Double engagementIndex;

        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder nodePath(String nodePath) { this.nodePath = nodePath; return this; }
        public Builder responseCount(Integer responseCount) { this.responseCount = responseCount; return this; }
        public Builder status(NodeAggregateStatus status) { this.status = status; return this; }
        public Builder enps(Double enps) { this.enps = enps; return this; }
        public Builder engagementIndex(Double engagementIndex) { this.engagementIndex = engagementIndex; return this; }

        public NodeAggregate build() {
            return new NodeAggregate(nodeId, nodePath, responseCount, status, enps, engagementIndex);
        }
    }
}
