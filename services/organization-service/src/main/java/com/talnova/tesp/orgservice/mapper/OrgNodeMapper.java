package com.talnova.tesp.orgservice.mapper;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.dto.CreateNodeDTO;
import com.talnova.tesp.orgservice.dto.OrgNodeResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class OrgNodeMapper {

    public OrgNodeDocument toDocument(CreateNodeDTO dto, String path, Integer depth) {
        if (dto == null) return null;

        return OrgNodeDocument.builder()
                .projectId(dto.getProjectId())
                .nodeId(dto.getNodeId())
                .name(dto.getName())
                .type(dto.getType())
                .parentId(dto.getParentId())
                .path(path)
                .depth(depth)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 1)
                .status(NodeStatus.ACTIVE)
                .attributes(dto.getAttributes())
                .version(1)
                .isDeleted(false)
                .build();
    }

    public OrgNodeResponseDTO toResponseDTO(OrgNodeDocument doc) {
        if (doc == null) return null;

        return OrgNodeResponseDTO.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .nodeId(doc.getNodeId())
                .name(doc.getName())
                .type(doc.getType())
                .parentId(doc.getParentId())
                .path(doc.getPath())
                .depth(doc.getDepth())
                .displayOrder(doc.getDisplayOrder())
                .status(doc.getStatus())
                .attributes(doc.getAttributes())
                .version(doc.getVersion())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
