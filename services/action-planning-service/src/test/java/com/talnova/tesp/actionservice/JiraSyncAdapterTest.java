package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.audit.ActionAuditLogger;
import com.talnova.tesp.actionservice.domain.model.ActionMilestone;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.domain.model.ExternalSyncInfo;
import com.talnova.tesp.actionservice.jira.JiraSyncAdapterServiceImpl;
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
class JiraSyncAdapterTest {

    @Mock
    private ActionPlanRepository actionPlanRepository;

    @Mock
    private ActionAuditLogger auditLogger;

    private JiraSyncAdapterServiceImpl jiraSyncAdapterService;

    @BeforeEach
    void setUp() {
        jiraSyncAdapterService = new JiraSyncAdapterServiceImpl(actionPlanRepository, auditLogger);
    }

    @Test
    @DisplayName("TC-ACT-501-01: Sync action plan to Jira Cloud issue per FR-ACT-004 and US-ACT-003")
    void testSyncActionPlanToJiraSuccess() {
        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .projectId("PRJ-99201")
                .status(ActionStatus.APPROVED)
                .build();

        when(actionPlanRepository.findByActionPlanId("ACT-901")).thenReturn(Optional.of(plan));
        when(actionPlanRepository.save(any(ActionPlanDocument.class))).thenAnswer(i -> i.getArgument(0));

        ExternalSyncInfo syncInfo = jiraSyncAdapterService.syncActionPlanToJira("ACT-901", "ENG");

        assertNotNull(syncInfo);
        assertEquals("JIRA", syncInfo.getSystem());
        assertTrue(syncInfo.getExternalKey().startsWith("ENG-"));
        assertNotNull(syncInfo.getLastSyncedAt());

        verify(actionPlanRepository, times(1)).save(any(ActionPlanDocument.class));
        verify(auditLogger, times(1)).logStateTransition(eq("ACT-901"), eq("SYSTEM_JIRA_SYNC"), eq(ActionStatus.APPROVED), eq(ActionStatus.APPROVED), anyString());
    }

    @Test
    @DisplayName("TC-ACT-501-02: Handle incoming Jira webhook when issue is status Done")
    void testHandleJiraWebhookDoneStatus() {
        ActionMilestone ms = ActionMilestone.builder()
                .milestoneId("MS-001")
                .title("Conduct Huddle")
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
        verify(actionPlanRepository, times(1)).save(any(ActionPlanDocument.class));
    }
}
