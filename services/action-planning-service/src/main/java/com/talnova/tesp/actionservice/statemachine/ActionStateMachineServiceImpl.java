package com.talnova.tesp.actionservice.statemachine;

import com.talnova.tesp.actionservice.audit.ActionAuditLogger;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.exception.InvalidStateTransitionException;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ActionStateMachineServiceImpl implements ActionStateMachineService {

    private static final Logger log = LoggerFactory.getLogger(ActionStateMachineServiceImpl.class);

    private final ActionPlanRepository actionPlanRepository;
    private final ActionAuditLogger auditLogger;

    @Autowired
    public ActionStateMachineServiceImpl(ActionPlanRepository actionPlanRepository, ActionAuditLogger auditLogger) {
        this.actionPlanRepository = actionPlanRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    public ActionPlanDocument transitionState(String actionPlanId, ActionStatus targetStatus, String actorId, String userRole, String details) {
        ActionPlanDocument plan = actionPlanRepository.findByActionPlanId(actionPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Action plan not found with ID: " + actionPlanId));

        ActionStatus current = plan.getStatus();
        log.info("Attempting state transition for ActionPlan '{}': {} -> {} by actor '{}' (Role: '{}')",
                actionPlanId, current, targetStatus, actorId, userRole);

        // Enforce valid state machine transitions per FR-ACT-002 and BR-ACT-001
        validateTransition(current, targetStatus, userRole);

        plan.setStatus(targetStatus);
        plan.setUpdatedAt(Instant.now());

        ActionPlanDocument saved = actionPlanRepository.save(plan);
        log.info("Successfully transitioned ActionPlan '{}' to state '{}'", actionPlanId, targetStatus);

        auditLogger.logStateTransition(actionPlanId, actorId, current, targetStatus, details);

        return saved;
    }

    private void validateTransition(ActionStatus current, ActionStatus target, String userRole) {
        if (target == ActionStatus.CANCELLED) {
            return; // Any state can transition to CANCELLED
        }

        switch (current) {
            case DRAFT:
                if (target != ActionStatus.PROPOSED) {
                    throw new InvalidStateTransitionException("DRAFT action plans can only transition to PROPOSED state");
                }
                break;

            case PROPOSED:
                if (target == ActionStatus.APPROVED || target == ActionStatus.REJECTED) {
                    if (!"HR_MANAGER".equalsIgnoreCase(userRole) && !"ADMIN".equalsIgnoreCase(userRole)) {
                        throw new InvalidStateTransitionException("HR_MANAGER approval is required to approve or reject a PROPOSED action plan (BR-ACT-001)");
                    }
                } else {
                    throw new InvalidStateTransitionException("PROPOSED action plans can only transition to APPROVED or REJECTED state");
                }
                break;

            case APPROVED:
                if (target != ActionStatus.IN_PROGRESS) {
                    throw new InvalidStateTransitionException("APPROVED action plans can only transition to IN_PROGRESS state");
                }
                break;

            case IN_PROGRESS:
                if (target != ActionStatus.COMPLETED) {
                    throw new InvalidStateTransitionException("IN_PROGRESS action plans can only transition to COMPLETED state");
                }
                break;

            case COMPLETED:
                if (target != ActionStatus.VERIFIED) {
                    throw new InvalidStateTransitionException("COMPLETED action plans can only transition to VERIFIED state upon post-action survey verification (BR-ACT-004)");
                }
                break;

            default:
                throw new InvalidStateTransitionException("No valid transitions available from state " + current);
        }
    }
}
