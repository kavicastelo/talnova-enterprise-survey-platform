package com.talnova.tesp.actionservice.listener;

import com.talnova.tesp.actionservice.event.AnalyticalSnapshotCreatedEvent;
import com.talnova.tesp.actionservice.generator.DraftActionPlanGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticalSnapshotListener {

    private static final Logger log = LoggerFactory.getLogger(AnalyticalSnapshotListener.class);
    public static final double SCORE_TRIGGER_THRESHOLD = 60.0;

    private final DraftActionPlanGeneratorService generatorService;

    @Autowired
    public AnalyticalSnapshotListener(DraftActionPlanGeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @KafkaListener(topics = "tesp.analytics.snapshots.v1", groupId = "action-planning-service-group")
    public boolean processSnapshotEvent(AnalyticalSnapshotCreatedEvent event) {
        if (event == null || event.getScore() == null) {
            log.warn("Received empty or null AnalyticalSnapshotCreatedEvent");
            return false;
        }

        log.info("Received AnalyticalSnapshotCreatedEvent for Node '{}', Group '{}', Category '{}', Score: {}%",
                event.getNodeId(), event.getGroupId(), event.getCategoryName(), event.getScore());

        if (event.getScore() < SCORE_TRIGGER_THRESHOLD) {
            log.warn("Score deficit detected ({}% < {}% threshold) for Node '{}' per FR-ACT-001 - Triggering Action Plan Draft pipeline",
                    event.getScore(), SCORE_TRIGGER_THRESHOLD, event.getNodeId());
            if (generatorService != null) {
                generatorService.generateDraftActionPlan(event);
            }
            return true;
        } else {
            log.info("Score {}% satisfies minimum threshold (>= {}%) for Node '{}' - No action plan required",
                    event.getScore(), SCORE_TRIGGER_THRESHOLD, event.getNodeId());
            return false;
        }
    }
}
