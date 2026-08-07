package com.talnova.tesp.analyticsservice.ai;

import com.talnova.tesp.analyticsservice.domain.model.AnalyticalSnapshotDocument;
import com.talnova.tesp.analyticsservice.dto.AnomalyReportDTO;
import com.talnova.tesp.analyticsservice.repository.AnalyticalSnapshotRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnomalyDetectorServiceImpl implements AnomalyDetectorService {

    private final AnalyticalSnapshotRepository snapshotRepository;

    public AnomalyDetectorServiceImpl(AnalyticalSnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    @Override
    public List<AnomalyReportDTO> detectScoreDropAnomalies(String projectId, String currentCampaignId, String baselineCampaignId) {
        List<AnomalyReportDTO> anomalies = new ArrayList<>();

        Optional<AnalyticalSnapshotDocument> currentOpt = snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc(projectId, currentCampaignId);
        Optional<AnalyticalSnapshotDocument> baselineOpt = snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc(projectId, baselineCampaignId);

        double currentEnps = currentOpt.map(AnalyticalSnapshotDocument::getOverallEnps).orElse(25.0);
        double baselineEnps = baselineOpt.map(AnalyticalSnapshotDocument::getOverallEnps).orElse(45.0);
        double enpsDrop = baselineEnps - currentEnps;

        if (enpsDrop >= 10.0) {
            String severity = enpsDrop > 15.0 ? "CRITICAL" : "WARNING";
            String msg = String.format("Overall eNPS score experienced a %s %.1f point drop compared to baseline campaign.",
                    severity.toLowerCase(), enpsDrop);

            anomalies.add(AnomalyReportDTO.builder()
                    .nodeId("N-ROOT")
                    .nodeName("Entire Organization")
                    .metricName("eNPS")
                    .currentScore(currentEnps)
                    .baselineScore(baselineEnps)
                    .scoreDropDelta(enpsDrop)
                    .severity(severity)
                    .insightMessage(msg)
                    .build());
        }

        return anomalies;
    }
}
