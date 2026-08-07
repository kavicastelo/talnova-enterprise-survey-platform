package com.talnova.tesp.actionservice.statemachine;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;

public interface ActionStateMachineService {

    /**
     * Executes state transitions on ActionPlanDocument enforcing role guards per FR-ACT-002 and BR-ACT-001.
     */
    ActionPlanDocument transitionState(String actionPlanId, ActionStatus targetStatus, String actorId, String userRole, String details);
}
