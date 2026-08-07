package com.talnova.tesp.actionservice.generator;

import com.talnova.tesp.actionservice.audit.ActionAuditLogger;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.event.AnalyticalSnapshotCreatedEvent;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class DraftActionPlanGeneratorServiceImpl implements DraftActionPlanGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(DraftActionPlanGeneratorServiceImpl.class);

    private final ActionPlanRepository actionPlanRepository;
    private final ActionAuditLogger auditLogger;

    @Autowired
    public DraftActionPlanGeneratorServiceImpl(ActionPlanRepository actionPlanRepository, ActionAuditLogger auditLogger) {
        this.actionPlanRepository = actionPlanRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    public ActionPlanDocument generateDraftActionPlan(AnalyticalSnapshotCreatedEvent event) {
        String actionPlanId = "ACT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant now = Instant.now();
        Instant targetDate = now.plusSeconds(86400L * 30); // Default 30-day target completion

        double baseline = event.getScore() != null ? event.getScore() : 0.0;
        double target = Math.min(100.0, baseline + 15.0); // +15% target score increase

        ActionPlanDocument draft = ActionPlanDocument.builder()
                .projectId(event.getProjectId())
                .actionPlanId(actionPlanId)
                .campaignId(event.getCampaignId())
                .nodeId(event.getNodeId())
                .groupId(event.getGroupId())
                .title("Remedial Action Plan: " + (event.getCategoryName() != null ? event.getCategoryName() : "Category Deficit"))
                .description("Automated draft action plan generated for score deficit (" + baseline + "% < 60.0% threshold) per FR-ACT-001.")
                .baselineScore(baseline)
                .targetScore(target)
                .status(ActionStatus.DRAFT)
                .assigneeId("UNASSIGNED")
                .createdBy("SYSTEM_AUTO_TRIGGER")
                .targetCompletionDate(targetDate)
                .milestones(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        ActionPlanDocument saved = actionPlanRepository.save(draft);
        log.info("Saved automated DRAFT Action Plan '{}' for Node '{}', Group '{}' (Baseline: {}%, Target: {}%)",
                actionPlanId, event.getNodeId(), event.getGroupId(), baseline, target);

        auditLogger.logStateTransition(actionPlanId, "SYSTEM_AUTO_TRIGGER", null, ActionStatus.DRAFT,
                "Automated DRAFT action plan generated due to low survey score (" + baseline + "%)");

        return saved;
    }
}
