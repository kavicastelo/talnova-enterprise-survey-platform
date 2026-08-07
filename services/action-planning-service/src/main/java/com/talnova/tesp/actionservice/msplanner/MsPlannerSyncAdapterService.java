package com.talnova.tesp.actionservice.msplanner;

import com.talnova.tesp.actionservice.domain.model.ExternalSyncInfo;

public interface MsPlannerSyncAdapterService {

    /**
     * Creates a Microsoft Planner Task via MS Graph API and binds externalSync metadata per FR-ACT-004.
     */
    ExternalSyncInfo syncActionPlanToPlanner(String actionPlanId, String planId);

    /**
     * Receives Microsoft Graph API change notification webhooks and updates TESP action milestones per FR-ACT-004.
     */
    boolean handlePlannerWebhook(String taskId, int percentComplete);
}
