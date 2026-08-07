package com.talnova.tesp.analyticsservice.service;

import com.talnova.tesp.analyticsservice.dto.LongitudinalDeltaDTO;

public interface LongitudinalAnalysisService {

    /**
     * Calculates baseline delta comparative scores between a current survey campaign and a historical baseline campaign.
     *
     * @param projectId Tenant project identifier
     * @param currentCampaignId Active or current campaign ID
     * @param baselineCampaignId Historical baseline campaign ID to compare against
     * @return LongitudinalDeltaDTO containing score deltas and trend direction indicator (UP, DOWN, STABLE).
     */
    LongitudinalDeltaDTO calculateLongitudinalDelta(String projectId, String currentCampaignId, String baselineCampaignId);
}
