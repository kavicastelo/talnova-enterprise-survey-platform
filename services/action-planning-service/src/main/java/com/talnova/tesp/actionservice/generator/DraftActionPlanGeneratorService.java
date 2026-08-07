package com.talnova.tesp.actionservice.generator;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.event.AnalyticalSnapshotCreatedEvent;

public interface DraftActionPlanGeneratorService {

    /**
     * Automatically generates a DRAFT Action Plan when a node category score drops below 60.0% per FR-ACT-001.
     */
    ActionPlanDocument generateDraftActionPlan(AnalyticalSnapshotCreatedEvent event);
}
