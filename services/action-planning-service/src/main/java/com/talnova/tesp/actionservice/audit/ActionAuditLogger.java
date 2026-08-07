package com.talnova.tesp.actionservice.audit;

import com.talnova.tesp.actionservice.domain.model.ActionStatus;

import java.util.List;

public interface ActionAuditLogger {

    /**
     * Records an immutable state transition or modification audit entry per FEAT-010 Sec 21.
     */
    ActionAuditLog logStateTransition(String actionPlanId, String actorId, ActionStatus oldStatus, ActionStatus newStatus, String details);

    /**
     * Retrieves historical audit entries for a given actionPlanId.
     */
    List<ActionAuditLog> getAuditHistory(String actionPlanId);
}
