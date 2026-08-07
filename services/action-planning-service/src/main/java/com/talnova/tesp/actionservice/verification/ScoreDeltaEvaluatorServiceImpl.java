package com.talnova.tesp.actionservice.verification;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.event.ActionPlanEventPublisher;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import com.talnova.tesp.actionservice.statemachine.ActionStateMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ScoreDeltaEvaluatorServiceImpl implements ScoreDeltaEvaluatorService {

    private static final Logger log = LoggerFactory.getLogger(ScoreDeltaEvaluatorServiceImpl.class);

    private final ActionPlanRepository actionPlanRepository;
    private final ActionStateMachineService stateMachineService;
    private final ActionPlanEventPublisher eventPublisher;

    @Autowired
    public ScoreDeltaEvaluatorServiceImpl(ActionPlanRepository actionPlanRepository, ActionStateMachineService stateMachineService, ActionPlanEventPublisher eventPublisher) {
        this.actionPlanRepository = actionPlanRepository;
        this.stateMachineService = stateMachineService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public double evaluateScoreDelta(String actionPlanId, double postActionScore) {
        ActionPlanDocument plan = actionPlanRepository.findByActionPlanId(actionPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Action plan not found with ID: " + actionPlanId));

        double baseline = plan.getBaselineScore() != null ? plan.getBaselineScore() : 0.0;
        double delta = postActionScore - baseline;

        plan.setPostActionScore(postActionScore);
        plan.setUpdatedAt(Instant.now());

        ActionPlanDocument updatedPlan = actionPlanRepository.save(plan);
        log.info("Evaluated score delta for ActionPlan '{}': Baseline={}%, PostAction={}%, Delta={}% per FR-ACT-006",
                actionPlanId, baseline, postActionScore, delta);

        if (delta > 0 && updatedPlan.getStatus() == ActionStatus.COMPLETED) {
            log.info("Positive score delta (+{}%) verified for ActionPlan '{}' - Auto-transitioning to VERIFIED per BR-ACT-004",
                    delta, actionPlanId);
            updatedPlan = stateMachineService.transitionState(
                    actionPlanId,
                    ActionStatus.VERIFIED,
                    "SYSTEM_VERIFICATION_ENGINE",
                    "SYSTEM",
                    "Auto-verified upon positive score delta (+" + delta + "%)"
            );
        }

        if (eventPublisher != null) {
            eventPublisher.publishStatusChangedEvent(updatedPlan, delta);
        }

        return delta;
    }
}
