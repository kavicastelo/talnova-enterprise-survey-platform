package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.audit.ActionAuditLoggerImpl;
import com.talnova.tesp.actionservice.domain.model.ActionMilestone;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.domain.model.ExternalSyncInfo;
import com.talnova.tesp.actionservice.event.AnalyticalSnapshotCreatedEvent;
import com.talnova.tesp.actionservice.exception.InvalidStateTransitionException;
import com.talnova.tesp.actionservice.generator.DraftActionPlanGeneratorServiceImpl;
import com.talnova.tesp.actionservice.jira.JiraSyncAdapterServiceImpl;
import com.talnova.tesp.actionservice.listener.AnalyticalSnapshotListener;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import com.talnova.tesp.actionservice.statemachine.ActionStateMachineServiceImpl;
import com.talnova.tesp.actionservice.verification.ScoreDeltaEvaluatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionTriggerAndSyncAuditTest {

    @Mock
    private ActionPlanRepository actionPlanRepository;

    private ActionAuditLoggerImpl auditLogger;
    private DraftActionPlanGeneratorServiceImpl generatorService;
    private AnalyticalSnapshotListener snapshotListener;
    private ActionStateMachineServiceImpl stateMachineService;
    private JiraSyncAdapterServiceImpl jiraSyncAdapterService;

    @BeforeEach
    void setUp() {
        auditLogger = new ActionAuditLoggerImpl();
        generatorService = new DraftActionPlanGeneratorServiceImpl(actionPlanRepository, auditLogger);
        snapshotListener = new AnalyticalSnapshotListener(generatorService);
        stateMachineService = new ActionStateMachineServiceImpl(actionPlanRepository, auditLogger);
        jiraSyncAdapterService = new JiraSyncAdapterServiceImpl(actionPlanRepository, auditLogger);
    }

    @Test
    @DisplayName("TC-ACT-001 / Threshold Audit: Score deficit 54.0% triggers DRAFT action plan generation with +15% target")
    void testThresholdTriggerAudit() {
        when(actionPlanRepository.save(any(ActionPlanDocument.class))).thenAnswer(i -> i.getArgument(0));

        AnalyticalSnapshotCreatedEvent event = AnalyticalSnapshotCreatedEvent.builder()
                .eventId("EVT-1001")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .nodeId("N-301")
                .groupId("GRP-COMMUNICATION")
                .categoryName("Leadership Communication")
                .score(54.0)
                .timestamp(Instant.now())
                .build();

        boolean triggered = snapshotListener.processSnapshotEvent(event);

        assertTrue(triggered, "54.0% score deficit must trigger draft action plan generation");
        verify(actionPlanRepository, times(1)).save(any(ActionPlanDocument.class));
    }

    @Test
    @DisplayName("BR-ACT-001 / HR Approval Audit: PROPOSED to APPROVED requires HR_MANAGER role")
    void testHrApprovalRoleGuardAudit() {
        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .status(ActionStatus.PROPOSED)
                .build();

        when(actionPlanRepository.findByActionPlanId("ACT-901")).thenReturn(Optional.of(plan));
        when(actionPlanRepository.save(any(ActionPlanDocument.class))).thenAnswer(i -> i.getArgument(0));

        // 1. Authorized HR_MANAGER approval
        ActionPlanDocument approved = stateMachineService.transitionState("ACT-901", ActionStatus.APPROVED, "USR-HR", "HR_MANAGER", "Approve budget");
        assertEquals(ActionStatus.APPROVED, approved.getStatus());

        // 2. Unauthorized DEPARTMENT_MANAGER attempt
        plan.setStatus(ActionStatus.PROPOSED);
        assertThrows(InvalidStateTransitionException.class, () ->
                stateMachineService.transitionState("ACT-901", ActionStatus.APPROVED, "USR-MGR", "DEPARTMENT_MANAGER", "Unauthorized")
        );
    }

    @Test
    @DisplayName("FR-ACT-004 / Jira Sync Audit: Bind Jira Issue Key and process status webhooks")
    void testJiraSyncAndWebhookAudit() {
        ActionMilestone ms = ActionMilestone.builder()
                .milestoneId("MS-001")
                .title("Town Hall")
                .completed(false)
                .build();

        ExternalSyncInfo syncInfo = ExternalSyncInfo.builder()
                .system("JIRA")
                .externalKey("ENG-402")
                .build();

        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .externalSync(syncInfo)
                .milestones(new ArrayList<>(List.of(ms)))
                .build();

        when(actionPlanRepository.findAll()).thenReturn(List.of(plan));
        when(actionPlanRepository.save(any(ActionPlanDocument.class))).thenAnswer(i -> i.getArgument(0));

        boolean processed = jiraSyncAdapterService.handleJiraWebhook("ENG-402", "Done");
        assertTrue(processed);
        assertTrue(plan.getMilestones().get(0).isCompleted());
    }
}
