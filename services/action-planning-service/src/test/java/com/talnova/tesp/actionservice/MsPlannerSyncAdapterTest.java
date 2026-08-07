package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.audit.ActionAuditLogger;
import com.talnova.tesp.actionservice.domain.model.ActionMilestone;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.domain.model.ExternalSyncInfo;
import com.talnova.tesp.actionservice.msplanner.MsPlannerSyncAdapterServiceImpl;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MsPlannerSyncAdapterTest {

    @Mock
    private ActionPlanRepository actionPlanRepository;

    @Mock
    private ActionAuditLogger auditLogger;

    private MsPlannerSyncAdapterServiceImpl msPlannerSyncAdapterService;

    @BeforeEach
    void setUp() {
        msPlannerSyncAdapterService = new MsPlannerSyncAdapterServiceImpl(actionPlanRepository, auditLogger);
    }

    @Test
    @DisplayName("TC-ACT-502-01: Sync action plan to Microsoft Planner task via Graph API per FR-ACT-004")
    void testSyncActionPlanToPlannerSuccess() {
        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .projectId("PRJ-99201")
                .status(ActionStatus.APPROVED)
                .build();

        when(actionPlanRepository.findByActionPlanId("ACT-901")).thenReturn(Optional.of(plan));
        when(actionPlanRepository.save(any(ActionPlanDocument.class))).thenAnswer(i -> i.getArgument(0));

        ExternalSyncInfo syncInfo = msPlannerSyncAdapterService.syncActionPlanToPlanner("ACT-901", "PLN-MAIN");

        assertNotNull(syncInfo);
        assertEquals("MS_PLANNER", syncInfo.getSystem());
        assertTrue(syncInfo.getExternalKey().startsWith("PLN-"));
        assertNotNull(syncInfo.getLastSyncedAt());

        verify(actionPlanRepository, times(1)).save(any(ActionPlanDocument.class));
        verify(auditLogger, times(1)).logStateTransition(eq("ACT-901"), eq("SYSTEM_MS_PLANNER_SYNC"), eq(ActionStatus.APPROVED), eq(ActionStatus.APPROVED), anyString());
    }

    @Test
    @DisplayName("TC-ACT-502-02: Handle incoming MS Planner Graph API webhook when percentComplete is 100")
    void testHandlePlannerWebhookComplete() {
        ActionMilestone ms = ActionMilestone.builder()
                .milestoneId("MS-001")
                .title("Conduct Huddle")
                .completed(false)
                .build();

        ExternalSyncInfo syncInfo = ExternalSyncInfo.builder()
                .system("MS_PLANNER")
                .externalKey("PLN-88102")
                .build();

        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .externalSync(syncInfo)
                .milestones(new ArrayList<>(List.of(ms)))
                .build();

        when(actionPlanRepository.findAll()).thenReturn(List.of(plan));
        when(actionPlanRepository.save(any(ActionPlanDocument.class))).thenAnswer(i -> i.getArgument(0));

        boolean processed = msPlannerSyncAdapterService.handlePlannerWebhook("PLN-88102", 100);

        assertTrue(processed);
        assertTrue(plan.getMilestones().get(0).isCompleted());
        verify(actionPlanRepository, times(1)).save(any(ActionPlanDocument.class));
    }
}
