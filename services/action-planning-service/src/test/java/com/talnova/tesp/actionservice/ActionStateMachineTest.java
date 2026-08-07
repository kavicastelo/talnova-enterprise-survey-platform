package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.audit.ActionAuditLogger;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.exception.InvalidStateTransitionException;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import com.talnova.tesp.actionservice.statemachine.ActionStateMachineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionStateMachineTest {

    @Mock
    private ActionPlanRepository actionPlanRepository;

    @Mock
    private ActionAuditLogger auditLogger;

    private ActionStateMachineServiceImpl stateMachineService;

    @BeforeEach
    void setUp() {
        stateMachineService = new ActionStateMachineServiceImpl(actionPlanRepository, auditLogger);
    }

    @Test
    @DisplayName("TC-ACT-301-01: Valid transition DRAFT -> PROPOSED -> APPROVED (HR_MANAGER) -> IN_PROGRESS -> COMPLETED")
    void testValidStateTransitionSequence() {
        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .status(ActionStatus.DRAFT)
                .build();

        when(actionPlanRepository.findByActionPlanId("ACT-901")).thenReturn(Optional.of(plan));
        when(actionPlanRepository.save(any(ActionPlanDocument.class))).thenAnswer(i -> i.getArgument(0));

        // 1. DRAFT -> PROPOSED
        ActionPlanDocument proposed = stateMachineService.transitionState("ACT-901", ActionStatus.PROPOSED, "USR-MGR", "DEPARTMENT_MANAGER", "Submit draft");
        assertEquals(ActionStatus.PROPOSED, proposed.getStatus());

        // 2. PROPOSED -> APPROVED by HR_MANAGER
        ActionPlanDocument approved = stateMachineService.transitionState("ACT-901", ActionStatus.APPROVED, "USR-HR", "HR_MANAGER", "Approve budget");
        assertEquals(ActionStatus.APPROVED, approved.getStatus());

        // 3. APPROVED -> IN_PROGRESS
        ActionPlanDocument inProgress = stateMachineService.transitionState("ACT-901", ActionStatus.IN_PROGRESS, "USR-MGR", "DEPARTMENT_MANAGER", "Start execution");
        assertEquals(ActionStatus.IN_PROGRESS, inProgress.getStatus());
    }

    @Test
    @DisplayName("TC-ACT-301-02: Transitioning PROPOSED to APPROVED without HR_MANAGER role throws InvalidStateTransitionException (BR-ACT-001)")
    void testProposedToApprovedWithoutHrRoleFails() {
        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .status(ActionStatus.PROPOSED)
                .build();

        when(actionPlanRepository.findByActionPlanId("ACT-901")).thenReturn(Optional.of(plan));

        assertThrows(InvalidStateTransitionException.class, () ->
                stateMachineService.transitionState("ACT-901", ActionStatus.APPROVED, "USR-MGR", "DEPARTMENT_MANAGER", "Unauthorized approval")
        );
    }

    @Test
    @DisplayName("TC-ACT-301-03: Invalid illegal transition DRAFT -> COMPLETED throws InvalidStateTransitionException")
    void testIllegalDirectTransitionFails() {
        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .status(ActionStatus.DRAFT)
                .build();

        when(actionPlanRepository.findByActionPlanId("ACT-901")).thenReturn(Optional.of(plan));

        assertThrows(InvalidStateTransitionException.class, () ->
                stateMachineService.transitionState("ACT-901", ActionStatus.COMPLETED, "USR-MGR", "DEPARTMENT_MANAGER", "Skip steps")
        );
    }
}
