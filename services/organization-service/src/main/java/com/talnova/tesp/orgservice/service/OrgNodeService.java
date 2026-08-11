package com.talnova.tesp.orgservice.service;

import com.talnova.tesp.orgservice.dto.CreateNodeDTO;
import com.talnova.tesp.orgservice.dto.MoveNodeRequestDTO;
import com.talnova.tesp.orgservice.dto.OrgNodeResponseDTO;

import java.util.List;

public interface OrgNodeService {

    OrgNodeResponseDTO createNode(CreateNodeDTO dto);

    OrgNodeResponseDTO getNodeByProjectIdAndNodeId(String projectId, String nodeId);

    OrgNodeResponseDTO moveNode(String projectId, String nodeId, MoveNodeRequestDTO moveRequest);

    List<OrgNodeResponseDTO> getSubTree(String projectId, String nodeId);

    List<OrgNodeResponseDTO> getLineage(String projectId, String nodeId);

    void deleteNode(String projectId, String nodeId);
}
