package com.talnova.tesp.orgservice.service;

import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.dto.HierarchyAnomalyReportDTO;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HierarchyAnomalyInspector {

    private static final Logger log = LoggerFactory.getLogger(HierarchyAnomalyInspector.class);

    private final OrgNodeRepository repository;

    public HierarchyAnomalyInspector(OrgNodeRepository repository) {
        this.repository = repository;
    }

    public HierarchyAnomalyReportDTO inspectAnomalies(String projectId) {
        List<OrgNodeDocument> allNodes = repository.findActiveSubTreeByPathPrefix(projectId, ",");
        Set<String> activeNodeIds = allNodes.stream()
                .map(OrgNodeDocument::getNodeId)
                .collect(Collectors.toSet());

        List<HierarchyAnomalyReportDTO.AnomalyDetail> anomalies = new ArrayList<>();

        // Group children by parentId
        Map<String, List<OrgNodeDocument>> childrenByParent = allNodes.stream()
                .filter(node -> node.getParentId() != null)
                .collect(Collectors.groupingBy(OrgNodeDocument::getParentId));

        for (OrgNodeDocument node : allNodes) {
            // Check 1: Extreme Depth Anomaly (> 20 levels)
            if (node.getDepth() != null && node.getDepth() > 20) {
                anomalies.add(HierarchyAnomalyReportDTO.AnomalyDetail.builder()
                        .anomalyType("EXTREME_DEPTH_WARNING")
                        .nodeId(node.getNodeId())
                        .severity("HIGH")
                        .message("Node '" + node.getNodeId() + "' has an extreme hierarchy depth level of " + node.getDepth() + ".")
                        .recommendation("Consider flattening organizational structure; recommended maximum hierarchy depth is 20 levels.")
                        .build());
            }

            // Check 2: Orphan Sub-Tree Anomaly (parentId non-null but parent missing)
            if (node.getParentId() != null && !node.getParentId().trim().isEmpty()) {
                if (!activeNodeIds.contains(node.getParentId())) {
                    anomalies.add(HierarchyAnomalyReportDTO.AnomalyDetail.builder()
                            .anomalyType("ORPHAN_SUBTREE_WARNING")
                            .nodeId(node.getNodeId())
                            .severity("CRITICAL")
                            .message("Node '" + node.getNodeId() + "' references non-existent active parentId '" + node.getParentId() + "'.")
                            .recommendation("Re-parent orphan node to an active parent node or restore parent node.")
                            .build());
                }
            }

            // Check 3: Single-Child Node Chain Anomaly (> 5 consecutive single-child levels)
            if (node.getDepth() != null && node.getDepth() >= 5) {
                List<OrgNodeDocument> siblings = childrenByParent.get(node.getParentId());
                if (siblings != null && siblings.size() == 1) {
                    // Check if parent also has single child
                    OrgNodeDocument parent = allNodes.stream()
                            .filter(n -> n.getNodeId().equals(node.getParentId()))
                            .findFirst()
                            .orElse(null);
                    if (parent != null && parent.getParentId() != null) {
                        List<OrgNodeDocument> parentSiblings = childrenByParent.get(parent.getParentId());
                        if (parentSiblings != null && parentSiblings.size() == 1) {
                            anomalies.add(HierarchyAnomalyReportDTO.AnomalyDetail.builder()
                                    .anomalyType("SINGLE_CHILD_CHAIN_WARNING")
                                    .nodeId(node.getNodeId())
                                    .severity("MEDIUM")
                                    .message("Node '" + node.getNodeId() + "' is part of a single-child administrative chain.")
                                    .recommendation("Consolidate single-child administrative node levels to streamline reporting structure.")
                                    .build());
                        }
                    }
                }
            }
        }

        log.info("AI Hierarchy Anomaly Inspection complete for project {}: {} nodes scanned, {} anomalies detected.",
                projectId, allNodes.size(), anomalies.size());

        return HierarchyAnomalyReportDTO.builder()
                .projectId(projectId)
                .totalNodesInspected(allNodes.size())
                .totalAnomaliesDetected(anomalies.size())
                .anomalies(anomalies)
                .build();
    }
}
