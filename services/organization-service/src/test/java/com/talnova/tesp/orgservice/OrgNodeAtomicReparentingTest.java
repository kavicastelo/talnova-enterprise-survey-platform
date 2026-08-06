package com.talnova.tesp.orgservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.domain.OutboxEventDocument;
import com.talnova.tesp.orgservice.dto.MoveNodeRequestDTO;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgNodeAtomicReparentingTest {

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
    @DisplayName("TC-ORG-302-A: Atomically re-parent node N-201 to N-102 and bulk update descendant paths")
    void testAtomicSubTreeReparenting() {
        OrgNodeDocument targetNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Engineering Department")
                .type("DEPARTMENT")
                .parentId("N-101")
                .path(",N-001,N-101,N-201,")
                .depth(3)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        OrgNodeDocument newParent = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-102")
                .name("Technology Division")
                .type("DIVISION")
                .path(",N-001,N-102,")
                .depth(2)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        OrgNodeDocument descendantChild = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .name("Backend Team")
                .type("TEAM")
                .parentId("N-201")
                .path(",N-001,N-101,N-201,N-301,")
                .depth(4)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-201")).thenReturn(Optional.of(targetNode));
        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-102")).thenReturn(Optional.of(newParent));
        when(repository.findActiveSubTreeByPathPrefix("PRJ-99201", ",N-001,N-101,N-201,"))
                .thenReturn(List.of(targetNode, descendantChild));

        when(repository.save(any(OrgNodeDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MoveNodeRequestDTO moveRequest = new MoveNodeRequestDTO("N-102");
        OrgNodeResponseDTO response = nodeService.moveNode("PRJ-99201", "N-201", moveRequest);

        verify(cycleDetectionGuard).validateMove("PRJ-99201", "N-201", "N-102");

        assertNotNull(response);
        assertEquals("N-201", response.getNodeId());
        assertEquals("N-102", response.getParentId());
        assertEquals(",N-001,N-102,N-201,", response.getPath());
        assertEquals(3, response.getDepth());

        // Verify OrgNodeMovedEvent recorded in outbox
        ArgumentCaptor<OutboxEventDocument> outboxCaptor = ArgumentCaptor.forClass(OutboxEventDocument.class);
        verify(outboxRepository).save(outboxCaptor.capture());
        assertEquals("ORG_NODE_MOVED", outboxCaptor.getValue().getEventType());

        // Verify Audit Log recorded
        verify(auditLoggerService).logOrgEvent(eq("PRJ-99201"), eq("MOVE_ORG_NODE"), eq("N-201"), any());

        // Verify descendant N-301 path updated to ,N-001,N-102,N-201,N-301,
        assertEquals(",N-001,N-102,N-201,N-301,", descendantChild.getPath());
        assertEquals(4, descendantChild.getDepth());
    }
}
