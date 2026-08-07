package com.talnova.tesp.actionservice.cron;

import com.talnova.tesp.actionservice.audit.ActionAuditLogger;
import com.talnova.tesp.actionservice.domain.model.ActionMilestone;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OverdueEscalationCronWorkerImpl implements OverdueEscalationCronWorker {

    private static final Logger log = LoggerFactory.getLogger(OverdueEscalationCronWorkerImpl.class);

    private final ActionPlanRepository actionPlanRepository;
    private final ActionAuditLogger auditLogger;

    @Autowired
    public OverdueEscalationCronWorkerImpl(ActionPlanRepository actionPlanRepository, ActionAuditLogger auditLogger) {
        this.actionPlanRepository = actionPlanRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    @Scheduled(cron = "0 0 8 * * ?") // Daily at 08:00 AM
    public int checkAndDispatchEscalationReminders() {
        log.info("Running daily milestone due date poller and overdue escalation cron per FR-ACT-005");

        List<ActionPlanDocument> activePlans = actionPlanRepository.findAll().stream()
                .filter(p -> p.getStatus() == ActionStatus.IN_PROGRESS || p.getStatus() == ActionStatus.APPROVED)
                .toList();

        Instant now = Instant.now();
        Instant threeDaysFuture = now.plusSeconds(86400L * 3);
        int notificationCount = 0;

        for (ActionPlanDocument plan : activePlans) {
            if (plan.getMilestones() == null) continue;

            for (ActionMilestone ms : plan.getMilestones()) {
                if (ms.isCompleted() || ms.getDueDate() == null) continue;

                // 1. Send reminder notification 3 days prior to due date
                if (ms.getDueDate().isBefore(threeDaysFuture) && ms.getDueDate().isAfter(now)) {
                    notificationCount++;
                    log.info("Reminder Nudge (3-day threshold): Milestone '{}' (ID: {}) for ActionPlan '{}' is due on {}",
                            ms.getTitle(), ms.getMilestoneId(), plan.getActionPlanId(), ms.getDueDate());
                    auditLogger.logStateTransition(plan.getActionPlanId(), "CRON_REMINDER_WORKER", plan.getStatus(), plan.getStatus(),
                            "Dispatched 3-day reminder nudge for milestone " + ms.getMilestoneId());
                }

                // 2. Escalate overdue milestones to HR (> 1 day past due)
                if (ms.getDueDate().isBefore(now.minusSeconds(86400L))) {
                    notificationCount++;
                    log.warn("OVERDUE ESCALATION TO HR: Milestone '{}' (ID: {}) for ActionPlan '{}' is OVERDUE (Due: {})",
                            ms.getTitle(), ms.getMilestoneId(), plan.getActionPlanId(), ms.getDueDate());
                    auditLogger.logStateTransition(plan.getActionPlanId(), "CRON_ESCALATION_WORKER", plan.getStatus(), plan.getStatus(),
                            "Escalated overdue milestone " + ms.getMilestoneId() + " to HR Manager");
                }
            }
        }

        log.info("Completed milestone due date polling; dispatched {} notifications/escalations", notificationCount);
        return notificationCount;
    }
}
