package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.audit.ActionAuditLog;
import com.talnova.tesp.actionservice.audit.ActionAuditLoggerImpl;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionAuditLoggerTest {

    private ActionAuditLoggerImpl auditLogger;

    @BeforeEach
    void setUp() {
        auditLogger = new ActionAuditLoggerImpl();
    }

    @Test
    @DisplayName("TC-ACT-102-01: Log state transition audit entry and query audit history per FEAT-010 Sec 21")
    void testLogStateTransitionAndQueryHistory() {
        ActionAuditLog log1 = auditLogger.logStateTransition(
                "ACT-901",
                "USR-DEPT-MGR",
                ActionStatus.DRAFT,
                ActionStatus.PROPOSED,
                "Submitted action plan for HR approval"
        );

        ActionAuditLog log2 = auditLogger.logStateTransition(
                "ACT-901",
                "USR-HR-DIR",
                ActionStatus.PROPOSED,
                ActionStatus.APPROVED,
                "Approved action plan budget and milestones"
        );

        assertNotNull(log1.getAuditId());
        assertTrue(log1.getAuditId().startsWith("AUD-"));
        assertEquals("ACT-901", log1.getActionPlanId());
        assertEquals("USR-DEPT-MGR", log1.getActorId());
        assertEquals(ActionStatus.DRAFT, log1.getOldStatus());
        assertEquals(ActionStatus.PROPOSED, log1.getNewStatus());

        List<ActionAuditLog> history = auditLogger.getAuditHistory("ACT-901");
        assertEquals(2, history.size());
        assertEquals(ActionStatus.APPROVED, history.get(1).getNewStatus());
    }
}
