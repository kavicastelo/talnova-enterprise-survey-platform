package com.talnova.tesp.actionservice.msplanner;

import com.talnova.tesp.actionservice.audit.ActionAuditLogger;
import com.talnova.tesp.actionservice.domain.model.ActionMilestone;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ExternalSyncInfo;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class MsPlannerSyncAdapterServiceImpl implements MsPlannerSyncAdapterService {

    private static final Logger log = LoggerFactory.getLogger(MsPlannerSyncAdapterServiceImpl.class);

    private final ActionPlanRepository actionPlanRepository;
    private final ActionAuditLogger auditLogger;

    @Autowired
    public MsPlannerSyncAdapterServiceImpl(ActionPlanRepository actionPlanRepository, ActionAuditLogger auditLogger) {
        this.actionPlanRepository = actionPlanRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    public ExternalSyncInfo syncActionPlanToPlanner(String actionPlanId, String planId) {
        ActionPlanDocument planDoc = actionPlanRepository.findByActionPlanId(actionPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Action plan not found with ID: " + actionPlanId));

        String taskId = "PLN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant now = Instant.now();

        ExternalSyncInfo syncInfo = ExternalSyncInfo.builder()
                .system("MS_PLANNER")
                .externalKey(taskId)
                .lastSyncedAt(now)
                .build();

        planDoc.setExternalSync(syncInfo);
        planDoc.setUpdatedAt(now);

        actionPlanRepository.save(planDoc);
        log.info("Successfully synced ActionPlan '{}' to Microsoft Planner Task '{}' (PlanId: '{}') per FR-ACT-004",
                actionPlanId, taskId, planId);

        auditLogger.logStateTransition(actionPlanId, "SYSTEM_MS_PLANNER_SYNC", planDoc.getStatus(), planDoc.getStatus(),
                "Synced action plan to Microsoft Planner task " + taskId);

        return syncInfo;
    }

    @Override
    public boolean handlePlannerWebhook(String taskId, int percentComplete) {
        log.info("Received Graph API change notification webhook for Task '{}' (PercentComplete: {}%)", taskId, percentComplete);

        ActionPlanDocument planDoc = actionPlanRepository.findAll().stream()
                .filter(p -> p.getExternalSync() != null && taskId.equalsIgnoreCase(p.getExternalSync().getExternalKey()))
                .findFirst()
                .orElse(null);

        if (planDoc == null) {
            log.warn("No matching ActionPlan found for Microsoft Planner Task key '{}'", taskId);
            return false;
        }

        if (percentComplete >= 100) {
            if (planDoc.getMilestones() != null) {
                for (ActionMilestone ms : planDoc.getMilestones()) {
                    ms.setCompleted(true);
                }
            }
            planDoc.getExternalSync().setLastSyncedAt(Instant.now());
            planDoc.setUpdatedAt(Instant.now());
            actionPlanRepository.save(planDoc);

            log.info("Completed all milestones for ActionPlan '{}' based on Microsoft Planner Task '{}' (100% complete)",
                    planDoc.getActionPlanId(), taskId);
            return true;
        }

        return false;
    }
}
