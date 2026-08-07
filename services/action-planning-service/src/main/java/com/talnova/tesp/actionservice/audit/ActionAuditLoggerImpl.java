package com.talnova.tesp.actionservice.audit;

import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActionAuditLoggerImpl implements ActionAuditLogger {

    private static final Logger log = LoggerFactory.getLogger(ActionAuditLoggerImpl.class);

    private final Map<String, List<ActionAuditLog>> auditStore = new ConcurrentHashMap<>();

    @Override
    public ActionAuditLog logStateTransition(String actionPlanId, String actorId, ActionStatus oldStatus, ActionStatus newStatus, String details) {
        String auditId = "AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant now = Instant.now();

        ActionAuditLog auditEntry = ActionAuditLog.builder()
                .auditId(auditId)
                .actionPlanId(actionPlanId)
                .actorId(actorId)
                .actionType("STATUS_CHANGE")
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .details(details)
                .timestamp(now)
                .build();

        auditStore.computeIfAbsent(actionPlanId, k -> Collections.synchronizedList(new ArrayList<>())).add(auditEntry);

        log.info("Audit Trail Logged [AuditID: {}] - ActionPlanId: '{}', Actor: '{}', Transition: {} -> {}, Details: '{}'",
                auditId, actionPlanId, actorId, oldStatus, newStatus, details);

        return auditEntry;
    }

    @Override
    public List<ActionAuditLog> getAuditHistory(String actionPlanId) {
        return new ArrayList<>(auditStore.getOrDefault(actionPlanId, Collections.emptyList()));
    }
}
