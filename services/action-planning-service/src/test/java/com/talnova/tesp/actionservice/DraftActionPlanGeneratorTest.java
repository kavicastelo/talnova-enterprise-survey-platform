package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.audit.ActionAuditLogger;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.event.AnalyticalSnapshotCreatedEvent;
import com.talnova.tesp.actionservice.generator.DraftActionPlanGeneratorServiceImpl;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DraftActionPlanGeneratorTest {

    @Mock
    private ActionPlanRepository actionPlanRepository;

    @Mock
    private ActionAuditLogger auditLogger;

    private DraftActionPlanGeneratorServiceImpl generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new DraftActionPlanGeneratorServiceImpl(actionPlanRepository, auditLogger);
    }

    @Test
    @DisplayName("TC-ACT-202-01: Generate DRAFT action plan with +15% target score and audit trail per FR-ACT-001 and US-ACT-001")
    void testGenerateDraftActionPlanSuccess() {
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

        ActionPlanDocument draft = generatorService.generateDraftActionPlan(event);

        assertNotNull(draft);
        assertTrue(draft.getActionPlanId().startsWith("ACT-"));
        assertEquals("PRJ-99201", draft.getProjectId());
        assertEquals("N-301", draft.getNodeId());
        assertEquals(54.0, draft.getBaselineScore());
        assertEquals(69.0, draft.getTargetScore(), 0.01, "Target score must be baseline + 15%");
        assertEquals(ActionStatus.DRAFT, draft.getStatus());
        assertEquals("UNASSIGNED", draft.getAssigneeId());

        verify(actionPlanRepository, times(1)).save(any(ActionPlanDocument.class));
        verify(auditLogger, times(1)).logStateTransition(eq(draft.getActionPlanId()), eq("SYSTEM_AUTO_TRIGGER"), isNull(), eq(ActionStatus.DRAFT), anyString());
    }
}
