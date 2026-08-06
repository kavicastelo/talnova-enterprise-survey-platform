package com.talnova.tesp.orgservice.service;

import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.exception.CircularHierarchyException;
import com.talnova.tesp.orgservice.exception.NodeValidationException;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CycleDetectionGuard {

    private final OrgNodeRepository repository;

    public CycleDetectionGuard(OrgNodeRepository repository) {
        this.repository = repository;
    }

    public void validateMove(String projectId, String nodeId, String newParentId) {
        if (newParentId == null || newParentId.trim().isEmpty()) {
            // Moving to root level is always safe
            return;
        }

        if (nodeId.equals(newParentId.trim())) {
            throw new CircularHierarchyException("A node cannot be set as its own parent.");
        }

        Optional<OrgNodeDocument> newParentOpt = repository.findActiveNodeByNodeId(projectId, newParentId);
        if (newParentOpt.isEmpty()) {
            throw new NodeValidationException("Target parent node '" + newParentId + "' does not exist in project '" + projectId + "'");
        }

        OrgNodeDocument newParent = newParentOpt.get();
        String targetAncestorSegment = "," + nodeId + ",";

        if (newParent.getPath().contains(targetAncestorSegment)) {
            throw new CircularHierarchyException(
                    "Circular hierarchy move detected: target parent node '" + newParentId + "' is a descendant of node '" + nodeId + "'"
            );
        }
    }
}
