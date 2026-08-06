package com.talnova.tesp.orgservice.dto;

import com.talnova.tesp.orgservice.validation.ValidNodeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Map;

@Schema(description = "Organization Node Creation Payload")
public class CreateNodeDTO {

    @NotBlank(message = "projectId is mandatory")
    @Pattern(regexp = "^PRJ-[A-Z0-9]{4,10}$", message = "projectId must match pattern ^PRJ-[A-Z0-9]{4,10}$")
    @Schema(example = "PRJ-99201")
    private String projectId;

    @NotBlank(message = "nodeId is mandatory")
    @Pattern(regexp = "^N-[A-Za-z0-9_-]{3,20}$", message = "nodeId must match pattern ^N-[A-Za-z0-9_-]{3,20}$")
    @Schema(example = "N-301")
    private String nodeId;

    @NotBlank(message = "Node name is mandatory")
    @Schema(example = "Engineering Department")
    private String name;

    @NotBlank(message = "Node type is mandatory")
    @ValidNodeType
    @Schema(example = "DEPARTMENT")
    private String type;

    @Schema(example = "N-201")
    private String parentId;

    @Schema(example = "1")
    private Integer displayOrder;

    @Schema(description = "Dynamic custom key-value demographic metadata attributes")
    private Map<String, Object> attributes;

    public CreateNodeDTO() {
    }

    public CreateNodeDTO(String projectId, String nodeId, String name, String type, String parentId,
                         Integer displayOrder, Map<String, Object> attributes) {
        this.projectId = projectId;
        this.nodeId = nodeId;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
        this.displayOrder = displayOrder;
        this.attributes = attributes;
    }

    public static Builder builder() {
        return new Builder();
    }

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

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }

    public static class Builder {
        private String projectId;
        private String nodeId;
        private String name;
        private String type;
        private String parentId;
        private Integer displayOrder;
        private Map<String, Object> attributes;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder parentId(String parentId) { this.parentId = parentId; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public Builder attributes(Map<String, Object> attributes) { this.attributes = attributes; return this; }

        public CreateNodeDTO build() {
            return new CreateNodeDTO(projectId, nodeId, name, type, parentId, displayOrder, attributes);
        }
    }
}
