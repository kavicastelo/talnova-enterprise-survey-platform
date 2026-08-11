package com.talnova.tesp.orgservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.domain.OutboxEventDocument;
import com.talnova.tesp.orgservice.domain.OutboxStatus;
import com.talnova.tesp.orgservice.dto.CreateNodeDTO;
import com.talnova.tesp.orgservice.dto.MoveNodeRequestDTO;
import com.talnova.tesp.orgservice.dto.OrgNodeResponseDTO;
import com.talnova.tesp.orgservice.exception.DuplicateNodeIdException;
import com.talnova.tesp.orgservice.exception.NodeNotFoundException;
import com.talnova.tesp.orgservice.mapper.OrgNodeMapper;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import com.talnova.tesp.orgservice.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrgNodeServiceImpl implements OrgNodeService {

    private static final Logger log = LoggerFactory.getLogger(OrgNodeServiceImpl.class);

    private final OrgNodeRepository repository;
    private final OutboxEventRepository outboxRepository;
    private final OrgNodeMapper mapper;
    private final PathCalculatorService pathCalculatorService;
    private final CycleDetectionGuard cycleDetectionGuard;
    private final OrgAuditLoggerService auditLoggerService;
    private final ObjectMapper objectMapper;

    public OrgNodeServiceImpl(OrgNodeRepository repository,
                              OutboxEventRepository outboxRepository,
                              OrgNodeMapper mapper,
                              PathCalculatorService pathCalculatorService,
                              CycleDetectionGuard cycleDetectionGuard,
                              OrgAuditLoggerService auditLoggerService,
                              ObjectMapper objectMapper) {
        this.repository = repository;
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
        this.pathCalculatorService = pathCalculatorService;
        this.cycleDetectionGuard = cycleDetectionGuard;
        this.auditLoggerService = auditLoggerService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public OrgNodeResponseDTO createNode(CreateNodeDTO dto) {
        if (repository.existsByProjectIdAndNodeIdAndIsDeletedFalse(dto.getProjectId(), dto.getNodeId())) {
            throw new DuplicateNodeIdException(dto.getNodeId());
        }

        PathCalculatorService.ComputedPathResult pathResult = pathCalculatorService.calculatePathAndDepth(
                dto.getProjectId(),
                dto.getNodeId(),
                dto.getParentId()
        );

        OrgNodeDocument doc = mapper.toDocument(dto, pathResult.getPath(), pathResult.getDepth());
        OrgNodeDocument savedDoc = repository.save(doc);

        // Record OrgNodeCreatedEvent transactional outbox record
        recordOutboxEvent(
                savedDoc.getProjectId(),
                "OrgNode",
                savedDoc.getNodeId(),
                "ORG_NODE_CREATED",
                Map.of(
                        "eventId", "EVT-" + UUID.randomUUID().toString().substring(0, 8),
                        "eventType", "ORG_NODE_CREATED",
                        "projectId", savedDoc.getProjectId(),
                        "nodeId", savedDoc.getNodeId(),
                        "name", savedDoc.getName(),
                        "type", savedDoc.getType(),
                        "parentId", savedDoc.getParentId() != null ? savedDoc.getParentId() : "",
                        "path", savedDoc.getPath(),
                        "depth", savedDoc.getDepth(),
                        "timestamp", Instant.now().toString()
                )
        );

        // Record Audit Log Entry
        auditLoggerService.logOrgEvent(
                savedDoc.getProjectId(),
                "CREATE_ORG_NODE",
                savedDoc.getNodeId(),
                Map.of("name", savedDoc.getName(), "type", savedDoc.getType(), "path", savedDoc.getPath())
        );

        return mapper.toResponseDTO(savedDoc);
    }

    @Override
    @Transactional(readOnly = true)
    public OrgNodeResponseDTO getNodeByProjectIdAndNodeId(String projectId, String nodeId) {
        OrgNodeDocument doc = repository.findActiveNodeByNodeId(projectId, nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));
        return mapper.toResponseDTO(doc);
    }

    @Override
    @Transactional
    public OrgNodeResponseDTO moveNode(String projectId, String nodeId, MoveNodeRequestDTO moveRequest) {
        String newParentId = moveRequest != null ? moveRequest.getNewParentId() : null;

        // Step 1: Validate DAG cycle prevention
        cycleDetectionGuard.validateMove(projectId, nodeId, newParentId);

        // Step 2: Fetch target node
        OrgNodeDocument targetNode = repository.findActiveNodeByNodeId(projectId, nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        String oldPath = targetNode.getPath();
        int oldDepth = targetNode.getDepth();

        // Step 3: Compute new path and depth for target node
        String newPath;
        int newDepth;

        if (newParentId == null || newParentId.trim().isEmpty()) {
            newPath = "," + nodeId + ",";
            newDepth = 1;
        } else {
            OrgNodeDocument parentDoc = repository.findActiveNodeByNodeId(projectId, newParentId)
                    .orElseThrow(() -> new NodeNotFoundException(newParentId));
            newPath = parentDoc.getPath() + nodeId + ",";
            newDepth = parentDoc.getDepth() + 1;
        }

        int depthDelta = newDepth - oldDepth;

        // Step 4: Save updated target node
        targetNode.setParentId(newParentId != null && !newParentId.trim().isEmpty() ? newParentId.trim() : null);
        targetNode.setPath(newPath);
        targetNode.setDepth(newDepth);
        OrgNodeDocument savedTarget = repository.save(targetNode);

        // Step 5: Bulk update descendant child nodes within transaction session
        List<OrgNodeDocument> descendants = repository.findActiveSubTreeByPathPrefix(projectId, oldPath);
        for (OrgNodeDocument descendant : descendants) {
            if (descendant.getNodeId().equals(nodeId)) {
                continue;
            }
            String descendantOldPath = descendant.getPath();
            String descendantNewPath = newPath + descendantOldPath.substring(oldPath.length());
            int descendantNewDepth = descendant.getDepth() + depthDelta;

            descendant.setPath(descendantNewPath);
            descendant.setDepth(descendantNewDepth);
            repository.save(descendant);
        }

        // Record OrgNodeMovedEvent transactional outbox record
        recordOutboxEvent(
                savedTarget.getProjectId(),
                "OrgNode",
                savedTarget.getNodeId(),
                "ORG_NODE_MOVED",
                Map.of(
                        "eventId", "EVT-" + UUID.randomUUID().toString().substring(0, 8),
                        "eventType", "ORG_NODE_MOVED",
                        "projectId", savedTarget.getProjectId(),
                        "nodeId", savedTarget.getNodeId(),
                        "oldPath", oldPath,
                        "newPath", newPath,
                        "timestamp", Instant.now().toString()
                )
        );

        // Record Audit Log Entry
        auditLoggerService.logOrgEvent(
                savedTarget.getProjectId(),
                "MOVE_ORG_NODE",
                savedTarget.getNodeId(),
                Map.of("oldPath", oldPath, "newPath", newPath, "descendantCount", descendants.size())
        );

        return mapper.toResponseDTO(savedTarget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgNodeResponseDTO> getSubTree(String projectId, String nodeId) {
        OrgNodeDocument targetNode = repository.findActiveNodeByNodeId(projectId, nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        List<OrgNodeDocument> descendants = repository.findActiveSubTreeByPathPrefix(projectId, targetNode.getPath());
        return descendants.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgNodeResponseDTO> getLineage(String projectId, String nodeId) {
        OrgNodeDocument targetNode = repository.findActiveNodeByNodeId(projectId, nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        String[] segments = targetNode.getPath().split(",");
        List<String> ancestorIds = new ArrayList<>();
        for (String segment : segments) {
            if (!segment.trim().isEmpty()) {
                ancestorIds.add(segment.trim());
            }
        }

        List<OrgNodeDocument> fetchedAncestors = repository.findAllActiveByNodeIds(projectId, ancestorIds);
        Map<String, OrgNodeDocument> docMap = fetchedAncestors.stream()
                .collect(Collectors.toMap(OrgNodeDocument::getNodeId, doc -> doc, (a, b) -> a));

        List<OrgNodeResponseDTO> result = new ArrayList<>();
        for (String ancestorId : ancestorIds) {
            OrgNodeDocument doc = docMap.get(ancestorId);
            if (doc != null) {
                result.add(mapper.toResponseDTO(doc));
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void deleteNode(String projectId, String nodeId) {
        log.info("Soft deleting organization node {} for projectId {}", nodeId, projectId);
        OrgNodeDocument node = repository.findActiveNodeByNodeId(projectId, nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        List<OrgNodeDocument> subTree = repository.findActiveSubTreeByPathPrefix(projectId, node.getPath());
        if (subTree.size() > 1) {
            throw new com.talnova.tesp.orgservice.exception.OrgNodeValidationException(
                    "Cannot delete node " + nodeId + " containing active child nodes (FR-ORG-006)");
        }

        node.setDeleted(true);
        node.setStatus("ARCHIVED");
        repository.save(node);

        String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8);
        recordOutboxEvent(projectId, "OrgNode", nodeId, "ORG_NODE_DELETED", Map.of(
                "eventId", eventId,
                "eventType", "ORG_NODE_DELETED",
                "projectId", projectId,
                "nodeId", nodeId
        ));
        auditLoggerService.logAuditEvent(projectId, "DELETE_ORG_NODE", "Soft deleted organization node " + nodeId, "127.0.0.1");
    }


    private void recordOutboxEvent(String projectId, String aggregateType, String aggregateId, String eventType, Map<String, Object> payloadMap) {
        try {
            String eventId = (String) payloadMap.get("eventId");
            String payloadJson = objectMapper.writeValueAsString(payloadMap);

            OutboxEventDocument outboxEvent = OutboxEventDocument.builder()
                    .eventId(eventId)
                    .projectId(projectId)
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payloadJson)
                    .status(OutboxStatus.PENDING)
                    .build();

            outboxRepository.save(outboxEvent);
            log.info("Recorded transactional outbox event {} of type {}", eventId, eventType);
        } catch (Exception ex) {
            log.error("Failed to serialize outbox event payload: {}", ex.getMessage(), ex);
        }
    }
}
