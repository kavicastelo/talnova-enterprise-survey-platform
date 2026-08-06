package com.talnova.tesp.orgservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Organization Node Re-parenting Move Request Payload")
public class MoveNodeRequestDTO {

    @Schema(description = "Target new parent nodeId (null or empty string to move to root level)", example = "N-102")
    private String newParentId;

    public MoveNodeRequestDTO() {
    }

    public MoveNodeRequestDTO(String newParentId) {
        this.newParentId = newParentId;
    }

    public String getNewParentId() { return newParentId; }
    public void setNewParentId(String newParentId) { this.newParentId = newParentId; }
}
