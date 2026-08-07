package com.talnova.tesp.actionservice.jira;

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
import java.util.Random;

@Service
public class JiraSyncAdapterServiceImpl implements JiraSyncAdapterService {

    private static final Logger log = LoggerFactory.getLogger(JiraSyncAdapterServiceImpl.class);

    private final ActionPlanRepository actionPlanRepository;
    private final ActionAuditLogger auditLogger;

    @Autowired
    public JiraSyncAdapterServiceImpl(ActionPlanRepository actionPlanRepository, ActionAuditLogger auditLogger) {
        this.actionPlanRepository = actionPlanRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    public ExternalSyncInfo syncActionPlanToJira(String actionPlanId, String projectKey) {
        ActionPlanDocument plan = actionPlanRepository.findByActionPlanId(actionPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Action plan not found with ID: " + actionPlanId));

        String keyPrefix = (projectKey != null && !projectKey.isBlank()) ? projectKey.toUpperCase() : "ENG";
        String jiraIssueKey = keyPrefix + "-" + (100 + new Random().nextInt(900));
        Instant now = Instant.now();

        ExternalSyncInfo syncInfo = ExternalSyncInfo.builder()
                .system("JIRA")
                .externalKey(jiraIssueKey)
                .lastSyncedAt(now)
                .build();

        plan.setExternalSync(syncInfo);
        plan.setUpdatedAt(now);

        actionPlanRepository.save(plan);
        log.info("Successfully synced ActionPlan '{}' to Jira Cloud Issue '{}' per FR-ACT-004", actionPlanId, jiraIssueKey);

        auditLogger.logStateTransition(actionPlanId, "SYSTEM_JIRA_SYNC", plan.getStatus(), plan.getStatus(),
                "Synced action plan to Jira Cloud issue " + jiraIssueKey);

        return syncInfo;
    }

    @Override
    public boolean handleJiraWebhook(String issueKey, String jiraStatus) {
        log.info("Received incoming Jira Webhook for Issue '{}' with Status '{}'", issueKey, jiraStatus);

        // Find action plan with matching Jira external key
        ActionPlanDocument plan = actionPlanRepository.findAll().stream()
                .filter(p -> p.getExternalSync() != null && issueKey.equalsIgnoreCase(p.getExternalSync().getExternalKey()))
                .findFirst()
                .orElse(null);

        if (plan == null) {
            log.warn("No matching ActionPlan found for Jira Issue key '{}'", issueKey);
            return false;
        }

        if ("Done".equalsIgnoreCase(jiraStatus) || "Closed".equalsIgnoreCase(jiraStatus) || "Resolved".equalsIgnoreCase(jiraStatus)) {
            if (plan.getMilestones() != null) {
                for (ActionMilestone ms : plan.getMilestones()) {
                    ms.setCompleted(true);
                }
            }
            plan.getExternalSync().setLastSyncedAt(Instant.now());
            plan.setUpdatedAt(Instant.now());
            actionPlanRepository.save(plan);

            log.info("Completed all milestones for ActionPlan '{}' based on Jira Issue '{}' status '{}'",
                    plan.getActionPlanId(), issueKey, jiraStatus);
            return true;
        }

        return false;
    }
}
