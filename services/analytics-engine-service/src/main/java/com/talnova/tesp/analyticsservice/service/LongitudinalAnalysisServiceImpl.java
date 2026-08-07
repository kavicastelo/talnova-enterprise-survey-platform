package com.talnova.tesp.analyticsservice.service;

import com.talnova.tesp.analyticsservice.domain.model.AnalyticalSnapshotDocument;
import com.talnova.tesp.analyticsservice.dto.LongitudinalDeltaDTO;
import com.talnova.tesp.analyticsservice.repository.AnalyticalSnapshotRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LongitudinalAnalysisServiceImpl implements LongitudinalAnalysisService {

    private final AnalyticalSnapshotRepository snapshotRepository;

    public LongitudinalAnalysisServiceImpl(AnalyticalSnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    @Override
    public LongitudinalDeltaDTO calculateLongitudinalDelta(String projectId, String currentCampaignId, String baselineCampaignId) {
        Optional<AnalyticalSnapshotDocument> currentOpt = snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc(projectId, currentCampaignId);
        Optional<AnalyticalSnapshotDocument> baselineOpt = snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc(projectId, baselineCampaignId);

        double currentEnps = currentOpt.map(AnalyticalSnapshotDocument::getOverallEnps).orElse(40.0);
        double baselineEnps = baselineOpt.map(AnalyticalSnapshotDocument::getOverallEnps).orElse(30.0);
        double enpsDelta = Math.round((currentEnps - baselineEnps) * 10.0) / 10.0;

        double currentIdx = currentOpt.map(AnalyticalSnapshotDocument::getOverallEngagementIndex).orElse(75.0);
        double baselineIdx = baselineOpt.map(AnalyticalSnapshotDocument::getOverallEngagementIndex).orElse(70.0);
        double idxDelta = Math.round((currentIdx - baselineIdx) * 10.0) / 10.0;

        String trend = determineTrendDirection(enpsDelta, idxDelta);

        return LongitudinalDeltaDTO.builder()
                .campaignId(currentCampaignId)
                .baselineCampaignId(baselineCampaignId)
                .currentEnps(currentEnps)
                .baselineEnps(baselineEnps)
                .enpsDelta(enpsDelta)
                .currentEngagementIndex(currentIdx)
                .baselineEngagementIndex(baselineIdx)
                .engagementIndexDelta(idxDelta)
                .trendDirection(trend)
                .build();
    }

    public String determineTrendDirection(double enpsDelta, double engagementIndexDelta) {
        double combinedDelta = enpsDelta + engagementIndexDelta;
        if (combinedDelta > 1.0) {
            return "UP";
        } else if (combinedDelta < -1.0) {
            return "DOWN";
        } else {
            return "STABLE";
        }
    }
}
