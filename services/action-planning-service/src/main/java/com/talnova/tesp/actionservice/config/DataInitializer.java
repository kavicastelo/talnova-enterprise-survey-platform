package com.talnova.tesp.actionservice.config;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ActionPlanRepository actionPlanRepository;

    public DataInitializer(ActionPlanRepository actionPlanRepository) {
        this.actionPlanRepository = actionPlanRepository;
    }

    @Override
    public void run(String... args) {
        seedActionPlans("PRJ-99201");
        seedActionPlans("PRJ-DEFAULT-001");
    }

    private void seedActionPlans(String projectId) {
        try {
            if (!actionPlanRepository.existsByProjectId(projectId)) {
                log.info("Seeding demo action plans for project '{}'...", projectId);

                ActionPlanDocument plan1 = ActionPlanDocument.builder()
                        .projectId(projectId)
                        .actionPlanId("ACT-101")
                        .campaignId("CMP-77102")
                        .nodeId("N-201")
                        .groupId("GRP-01")
                        .title("Enhance Remote Work IT Infrastructure")
                        .description("Upgrade VPN bandwidth and supply ergonomic home office kits.")
                        .baselineScore(64.5)
                        .targetScore(85.0)
                        .status(ActionStatus.APPROVED)
                        .assigneeId("EMP-10020")
                        .createdBy("usr_admin_01")
                        .targetCompletionDate(Instant.now().plus(30, ChronoUnit.DAYS))
                        .milestones(Collections.emptyList())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                ActionPlanDocument plan2 = ActionPlanDocument.builder()
                        .projectId(projectId)
                        .actionPlanId("ACT-102")
                        .campaignId("CMP-77102")
                        .nodeId("N-301")
                        .groupId("GRP-02")
                        .title("Leadership Communication Workshops")
                        .description("Conduct bi-weekly town halls with executive leadership.")
                        .baselineScore(58.0)
                        .targetScore(78.0)
                        .status(ActionStatus.IN_PROGRESS)
                        .assigneeId("EMP-10021")
                        .createdBy("usr_admin_01")
                        .targetCompletionDate(Instant.now().plus(45, ChronoUnit.DAYS))
                        .milestones(Collections.emptyList())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                actionPlanRepository.save(plan1);
                actionPlanRepository.save(plan2);
                log.info("Seeded action plans ACT-101 and ACT-102 for project '{}'", projectId);
            }
        } catch (Exception ex) {
            log.error("ActionPlan DataInitializer error for {}:", projectId, ex);
        }
    }
}
