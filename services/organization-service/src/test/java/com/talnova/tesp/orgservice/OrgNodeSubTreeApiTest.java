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
class OrgNodeSubTreeApiTest {

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
    @DisplayName("TC-ORG-401-A: getSubTree returns all descendant nodes matching target node's materialized path prefix")
    void testGetSubTreeSuccess() {
        OrgNodeDocument targetNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-101")
                .name("Engineering Division")
                .type("DIVISION")
                .path(",N-001,N-101,")
                .depth(2)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        OrgNodeDocument child1 = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Software Department")
                .type("DEPARTMENT")
                .path(",N-001,N-101,N-201,")
                .depth(3)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        OrgNodeDocument child2 = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .name("Backend Team")
                .type("TEAM")
                .path(",N-001,N-101,N-201,N-301,")
                .depth(4)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-101")).thenReturn(Optional.of(targetNode));
        when(repository.findActiveSubTreeByPathPrefix("PRJ-99201", ",N-001,N-101,"))
                .thenReturn(List.of(targetNode, child1, child2));

        List<OrgNodeResponseDTO> subTree = nodeService.getSubTree("PRJ-99201", "N-101");

        assertNotNull(subTree);
        assertEquals(3, subTree.size());
        assertEquals("N-101", subTree.get(0).getNodeId());
        assertEquals("N-201", subTree.get(1).getNodeId());
        assertEquals("N-301", subTree.get(2).getNodeId());
        assertEquals(",N-001,N-101,N-201,N-301,", subTree.get(2).getPath());
    }
}
