package com.talnova.tesp.configservice;

import com.talnova.tesp.common.context.ProjectContextFilter;
import com.talnova.tesp.common.context.ProjectContextHolder;
import com.talnova.tesp.configservice.domain.AuditLogDocument;
import com.talnova.tesp.configservice.repository.AuditLogRepository;
import com.talnova.tesp.configservice.service.AuditLoggerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultiTenantSecurityAuditTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditLoggerService auditLoggerService;
    private ProjectContextFilter projectContextFilter;

    @BeforeEach
    void setUp() {
        auditLoggerService = new AuditLoggerService(auditLogRepository);
        projectContextFilter = new ProjectContextFilter();
    }

    @AfterEach
    void tearDown() {
        ProjectContextHolder.clear();
    }

    @Test
    @DisplayName("TC-CFG-701-A: ProjectContextFilter populates ThreadLocal context from headers")
    void testProjectContextFilterPopulatesThreadLocal() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ProjectContextFilter.HEADER_PROJECT_ID, "PRJ-99201");
        request.addHeader(ProjectContextFilter.HEADER_USER_ID, "USR-ADMIN-88");
        request.addHeader(ProjectContextFilter.HEADER_CORRELATION_ID, "CORR-12345");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
            assertEquals("PRJ-99201", ProjectContextHolder.getProjectId());
            assertEquals("USR-ADMIN-88", ProjectContextHolder.getUserId());
            assertEquals("CORR-12345", ProjectContextHolder.getCorrelationId());
        };

        projectContextFilter.doFilter(request, response, filterChain);

        // Assert context is cleaned up after filter execution
        assertNull(ProjectContextHolder.getProjectId());
        assertNull(ProjectContextHolder.getUserId());
    }

    @Test
    @DisplayName("TC-CFG-701-B: AuditLoggerService records audit event with actor ID and IP address")
    void testAuditLoggerRecordsAuditEvent() {
        ProjectContextHolder.setUserId("USR-ADMIN-88");
        ProjectContextHolder.setCorrelationId("CORR-12345");

        auditLoggerService.logAuditEvent("PRJ-99201", "UPDATE_PROJECT", "Updated primary brand color", "192.168.1.50");

        ArgumentCaptor<AuditLogDocument> auditCaptor = ArgumentCaptor.forClass(AuditLogDocument.class);
        verify(auditLogRepository, times(1)).save(auditCaptor.capture());

        AuditLogDocument savedAudit = auditCaptor.getValue();
        assertEquals("PRJ-99201", savedAudit.getProjectId());
        assertEquals("USR-ADMIN-88", savedAudit.getUserId());
        assertEquals("UPDATE_PROJECT", savedAudit.getAction());
        assertEquals("192.168.1.50", savedAudit.getIpAddress());
        assertEquals("CORR-12345", savedAudit.getCorrelationId());
        assertTrue(savedAudit.getAuditId().startsWith("AUD-"));
    }
}
