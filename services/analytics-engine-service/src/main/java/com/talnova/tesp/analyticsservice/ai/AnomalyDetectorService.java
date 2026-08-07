package com.talnova.tesp.analyticsservice.ai;

import com.talnova.tesp.analyticsservice.dto.AnomalyReportDTO;

import java.util.List;

public interface AnomalyDetectorService {

    /**
     * Analyzes current campaign score metrics against baseline campaign, detecting significant score drop anomalies (> 10 points).
     *
     * @param projectId Tenant project identifier
     * @param currentCampaignId Active campaign ID
     * @param baselineCampaignId Historical baseline campaign ID
     * @return List of detected AnomalyReportDTO insights with severity rating (CRITICAL or WARNING).
     */
    List<AnomalyReportDTO> detectScoreDropAnomalies(String projectId, String currentCampaignId, String baselineCampaignId);
}
