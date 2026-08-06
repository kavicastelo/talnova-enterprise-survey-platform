package com.talnova.tesp.orgservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.dto.OrgNodeResponseDTO;
import com.talnova.tesp.orgservice.mapper.OrgNodeMapper;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import com.talnova.tesp.orgservice.repository.OutboxEventRepository;
import com.talnova.tesp.orgservice.service.CycleDetectionGuard;
import com.talnova.tesp.orgservice.service.OrgAuditLoggerService;
import com.talnova.tesp.orgservice.service.OrgNodeServiceImpl;
import com.talnova.tesp.orgservice.service.PathCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgNodeLineageApiTest {

    @Mock
    private OrgNodeRepository repository;

    @Mock
    private OutboxEventRepository outboxRepository;

    @Mock
    private PathCalculatorService pathCalculatorService;

    @Mock
    private CycleDetectionGuard cycleDetectionGuard;

    @Mock
    private OrgAuditLoggerService auditLoggerService;

    private OrgNodeMapper mapper;
    private ObjectMapper objectMapper;
    private OrgNodeServiceImpl nodeService;

    @BeforeEach
    void setUp() {
        mapper = new OrgNodeMapper();
        objectMapper = new ObjectMapper();
        nodeService = new OrgNodeServiceImpl(repository, outboxRepository, mapper, pathCalculatorService, cycleDetectionGuard, auditLoggerService, objectMapper);
    }

    @Test
    @DisplayName("TC-ORG-402-A: getLineage resolves ordered array of ancestor nodes from Root to leaf FR-ORG-005")
    void testGetLineageSuccess() {
        OrgNodeDocument leafNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .name("Backend Team")
                .type("TEAM")
                .path(",N-001,N-101,N-201,N-301,")
                .depth(4)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        OrgNodeDocument rootNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-001")
                .name("Aitken Spence PLC")
                .type("COMPANY")
                .path(",N-001,")
                .depth(1)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        OrgNodeDocument divNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-101")
                .name("Engineering Division")
                .type("DIVISION")
                .path(",N-001,N-101,")
                .depth(2)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        OrgNodeDocument deptNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Software Department")
                .type("DEPARTMENT")
                .path(",N-001,N-101,N-201,")
                .depth(3)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-301")).thenReturn(Optional.of(leafNode));
        when(repository.findAllActiveByNodeIds("PRJ-99201", List.of("N-001", "N-101", "N-201", "N-301")))
                .thenReturn(List.of(rootNode, divNode, deptNode, leafNode));

        List<OrgNodeResponseDTO> lineage = nodeService.getLineage("PRJ-99201", "N-301");

        assertNotNull(lineage);
        assertEquals(4, lineage.size());
        assertEquals("N-001", lineage.get(0).getNodeId());
        assertEquals("N-101", lineage.get(1).getNodeId());
        assertEquals("N-201", lineage.get(2).getNodeId());
        assertEquals("N-301", lineage.get(3).getNodeId());
    }
}
