package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.audit.ActionAuditLogger;
import com.talnova.tesp.actionservice.cron.OverdueEscalationCronWorkerImpl;
import com.talnova.tesp.actionservice.domain.model.ActionMilestone;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OverdueEscalationCronTest {

    @Mock
    private ActionPlanRepository actionPlanRepository;

    @Mock
    private ActionAuditLogger auditLogger;

    private OverdueEscalationCronWorkerImpl cronWorker;

    @BeforeEach
    void setUp() {
        cronWorker = new OverdueEscalationCronWorkerImpl(actionPlanRepository, auditLogger);
    }

    @Test
    @DisplayName("TC-ACT-601-01: Poll due dates, dispatch 3-day reminder nudge, and escalate overdue milestone per FR-ACT-005")
    void testCheckAndDispatchEscalationReminders() {
        Instant now = Instant.now();

        // Milestone 1: Due in 2 days (triggers 3-day reminder nudge)
        ActionMilestone ms1 = ActionMilestone.builder()
                .milestoneId("MS-001")
                .title("Schedule Town Hall")
                .completed(false)
                .dueDate(now.plusSeconds(86400L * 2))
                .build();

        // Milestone 2: Overdue by 2 days (triggers HR escalation)
        ActionMilestone ms2 = ActionMilestone.builder()
                .milestoneId("MS-002")
                .title("Publish Q&A Summary")
                .completed(false)
                .dueDate(now.minusSeconds(86400L * 2))
                .build();

        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .status(ActionStatus.IN_PROGRESS)
                .milestones(List.of(ms1, ms2))
                .build();

        when(actionPlanRepository.findAll()).thenReturn(List.of(plan));

        int dispatched = cronWorker.checkAndDispatchEscalationReminders();

        assertEquals(2, dispatched, "Must dispatch 1 reminder nudge + 1 overdue escalation notification");
        verify(auditLogger, times(2)).logStateTransition(eq("ACT-901"), anyString(), eq(ActionStatus.IN_PROGRESS), eq(ActionStatus.IN_PROGRESS), anyString());
    }
}
