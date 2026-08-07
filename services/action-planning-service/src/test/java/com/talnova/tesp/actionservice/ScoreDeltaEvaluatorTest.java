package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.event.ActionPlanEventPublisher;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import com.talnova.tesp.actionservice.statemachine.ActionStateMachineService;
import com.talnova.tesp.actionservice.verification.ScoreDeltaEvaluatorServiceImpl;
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
class ScoreDeltaEvaluatorTest {

    @Mock
    private ActionPlanRepository actionPlanRepository;

    @Mock
    private ActionStateMachineService stateMachineService;

    @Mock
    private ActionPlanEventPublisher eventPublisher;

    private ScoreDeltaEvaluatorServiceImpl evaluatorService;

    @BeforeEach
    void setUp() {
        evaluatorService = new ScoreDeltaEvaluatorServiceImpl(actionPlanRepository, stateMachineService, eventPublisher);
    }

    @Test
    @DisplayName("TC-ACT-701-01: Calculate score improvement delta and auto-transition to VERIFIED on positive delta per BR-ACT-004")
    void testEvaluateScoreDeltaSuccessAndAutoVerify() {
        ActionPlanDocument plan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .baselineScore(54.0)
                .status(ActionStatus.COMPLETED)
                .build();

        ActionPlanDocument verifiedPlan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .baselineScore(54.0)
                .postActionScore(70.0)
                .status(ActionStatus.VERIFIED)
                .build();

        when(actionPlanRepository.findByActionPlanId("ACT-901")).thenReturn(Optional.of(plan));
        when(actionPlanRepository.save(any(ActionPlanDocument.class))).thenAnswer(i -> i.getArgument(0));
        when(stateMachineService.transitionState(eq("ACT-901"), eq(ActionStatus.VERIFIED), anyString(), anyString(), anyString()))
                .thenReturn(verifiedPlan);

        double delta = evaluatorService.evaluateScoreDelta("ACT-901", 70.0);

        assertEquals(16.0, delta, 0.01, "Delta must be +16.0%");
        assertEquals(70.0, plan.getPostActionScore());

        verify(actionPlanRepository, times(1)).save(any(ActionPlanDocument.class));
        verify(stateMachineService, times(1)).transitionState(eq("ACT-901"), eq(ActionStatus.VERIFIED), anyString(), anyString(), anyString());
        verify(eventPublisher, times(1)).publishStatusChangedEvent(eq(verifiedPlan), eq(16.0));
    }
}
