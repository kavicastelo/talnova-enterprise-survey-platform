package com.talnova.tesp.actionservice.jira;

import com.talnova.tesp.actionservice.domain.model.ExternalSyncInfo;

public interface JiraSyncAdapterService {

    /**
     * Creates a Jira Cloud Issue for an Action Plan and binds externalSync metadata per FR-ACT-004 and US-ACT-003.
     */
    ExternalSyncInfo syncActionPlanToJira(String actionPlanId, String projectKey);

    /**
     * Receives Jira status webhook notifications and updates TESP action milestones per FR-ACT-004.
     */
    boolean handleJiraWebhook(String issueKey, String jiraStatus);
}
