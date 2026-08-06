package com.talnova.tesp.orgservice.dto;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Organization Node Response Payload")
public class OrgNodeResponseDTO {

    @Schema(example = "66b26d8f8a84a51e3c8b9999")
    private String id;

    @Schema(example = "PRJ-99201")
    private String projectId;

    @Schema(example = "N-301")
    private String nodeId;

    @Schema(example = "Engineering Department")
    private String name;

    @Schema(example = "DEPARTMENT")
    private String type;

    @Schema(example = "N-201")
    private String parentId;

    @Schema(example = ",N-001,N-101,N-201,N-301,")
    private String path;

    @Schema(example = "4")
    private Integer depth;

    @Schema(example = "1")
    private Integer displayOrder;

    @Schema(example = "ACTIVE")
    private NodeStatus status;

    private Map<String, Object> attributes;

    @Schema(example = "1")
    private Integer version;

    private Instant createdAt;
    private Instant updatedAt;

    public OrgNodeResponseDTO() {
    }

    public OrgNodeResponseDTO(String id, String projectId, String nodeId, String name, String type,
                              String parentId, String path, Integer depth, Integer displayOrder,
                              NodeStatus status, Map<String, Object> attributes, Integer version,
                              Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.nodeId = nodeId;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
        this.path = path;
        this.depth = depth;
        this.displayOrder = displayOrder;
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

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Integer getDepth() { return depth; }
    public void setDepth(Integer depth) { this.depth = depth; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public NodeStatus getStatus() { return status; }
    public void setStatus(NodeStatus status) { this.status = status; }

    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String nodeId;
        private String name;
        private String type;
        private String parentId;
        private String path;
        private Integer depth;
        private Integer displayOrder;
        private NodeStatus status;
        private Map<String, Object> attributes;
        private Integer version;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder parentId(String parentId) { this.parentId = parentId; return this; }
        public Builder path(String path) { this.path = path; return this; }
        public Builder depth(Integer depth) { this.depth = depth; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public Builder status(NodeStatus status) { this.status = status; return this; }
        public Builder attributes(Map<String, Object> attributes) { this.attributes = attributes; return this; }
        public Builder version(Integer version) { this.version = version; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public OrgNodeResponseDTO build() {
            return new OrgNodeResponseDTO(id, projectId, nodeId, name, type, parentId, path, depth, displayOrder, status, attributes, version, createdAt, updatedAt);
        }
    }
}
