package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.domain.AuditLogDocument;
import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.repository.AuditLogRepository;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import com.talnova.tesp.orgservice.security.SubTreeScopeInterceptor;
import com.talnova.tesp.orgservice.service.OrgAuditLoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubTreeScopeSecurityAuditTest {

    @Mock
    private OrgNodeRepository repository;

    @Mock
    private AuditLogRepository auditLogRepository;

    private SubTreeScopeInterceptor scopeInterceptor;
    private OrgAuditLoggerService auditLoggerService;

    @BeforeEach
    void setUp() {
        scopeInterceptor = new SubTreeScopeInterceptor(repository);
        auditLoggerService = new OrgAuditLoggerService(auditLogRepository);
    }

    @Test
    @DisplayName("TC-ORG-801-A: SubTreeScopeInterceptor permits access when requested node is inside X-Node-Scope path PR-ORG-001")
    void testScopeInterceptorAllowsAuthorizedAccess() throws Exception {
        OrgNodeDocument node = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .path(",N-001,N-101,N-201,N-301,")
                .depth(4)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-301")).thenReturn(Optional.of(node));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/nodes/N-301/subtree");
        request.setParameter("projectId", "PRJ-99201");
        request.addHeader("X-Node-Scope", ",N-001,N-101,");

        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = scopeInterceptor.preHandle(request, response, new Object());
        assertTrue(allowed);
    }

    @Test
    @DisplayName("TC-ORG-801-B: SubTreeScopeInterceptor throws AccessDeniedException when requested node is outside X-Node-Scope path PR-ORG-001")
    void testScopeInterceptorBlocksUnauthorizedAccess() {
        OrgNodeDocument unauthorizedNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-999")
                .path(",N-001,N-102,N-999,")
                .depth(3)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-999")).thenReturn(Optional.of(unauthorizedNode));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/nodes/N-999/subtree");
        request.setParameter("projectId", "PRJ-99201");
        request.addHeader("X-Node-Scope", ",N-001,N-101,");

        MockHttpServletResponse response = new MockHttpServletResponse();

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                scopeInterceptor.preHandle(request, response, new Object()));

        assertTrue(exception.getMessage().contains("outside authorized sub-tree scope"));
    }

    @Test
    @DisplayName("TC-ORG-801-C: OrgAuditLoggerService writes audit log entry to AuditLogRepository DOC-010")
    void testAuditLoggerServiceWritesLog() {
        auditLoggerService.logOrgEvent("PRJ-99201", "MOVE_ORG_NODE", "N-201", Map.of("oldPath", ",N-001,N-101,N-201,"));

        ArgumentCaptor<AuditLogDocument> captor = ArgumentCaptor.forClass(AuditLogDocument.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLogDocument savedAudit = captor.getValue();
        assertEquals("PRJ-99201", savedAudit.getProjectId());
        assertEquals("MOVE_ORG_NODE", savedAudit.getAction());
        assertEquals("N-201", savedAudit.getResourceId());
        assertNotNull(savedAudit.getTimestamp());
    }
}
