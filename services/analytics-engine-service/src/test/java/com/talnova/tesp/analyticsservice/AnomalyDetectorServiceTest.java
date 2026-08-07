package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.ai.AnomalyDetectorServiceImpl;
import com.talnova.tesp.analyticsservice.domain.model.AnalyticalSnapshotDocument;
import com.talnova.tesp.analyticsservice.dto.AnomalyReportDTO;
import com.talnova.tesp.analyticsservice.repository.AnalyticalSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnomalyDetectorServiceTest {

    @Mock
    private AnalyticalSnapshotRepository snapshotRepository;

    private AnomalyDetectorServiceImpl anomalyDetector;

    @BeforeEach
    void setUp() {
        anomalyDetector = new AnomalyDetectorServiceImpl(snapshotRepository);
    }

    @Test
    @DisplayName("TC-ANL-701-01: Detect CRITICAL anomaly for score drop > 15.0 points")
    void testDetectCriticalAnomaly() {
        AnalyticalSnapshotDocument currentDoc = AnalyticalSnapshotDocument.builder()
                .campaignId("CMP-2026")
                .overallEnps(20.0)
                .build();

        AnalyticalSnapshotDocument baselineDoc = AnalyticalSnapshotDocument.builder()
                .campaignId("CMP-2025")
                .overallEnps(40.0)
                .build();

        when(snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc("PRJ-99201", "CMP-2026"))
                .thenReturn(Optional.of(currentDoc));
        when(snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc("PRJ-99201", "CMP-2025"))
                .thenReturn(Optional.of(baselineDoc));

        List<AnomalyReportDTO> anomalies = anomalyDetector.detectScoreDropAnomalies("PRJ-99201", "CMP-2026", "CMP-2025");

        assertNotNull(anomalies);
        assertEquals(1, anomalies.size());
        assertEquals("CRITICAL", anomalies.get(0).getSeverity(), "Drop of 20 points (> 15) must trigger CRITICAL severity");
    }

    @Test
    @DisplayName("TC-ANL-701-02: Detect WARNING anomaly for score drop between 10.0 and 15.0 points")
    void testDetectWarningAnomaly() {
        AnalyticalSnapshotDocument currentDoc = AnalyticalSnapshotDocument.builder()
                .campaignId("CMP-2026")
                .overallEnps(28.0)
                .build();

        AnalyticalSnapshotDocument baselineDoc = AnalyticalSnapshotDocument.builder()
                .campaignId("CMP-2025")
                .overallEnps(40.0)
                .build();

        when(snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc("PRJ-99201", "CMP-2026"))
                .thenReturn(Optional.of(currentDoc));
        when(snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc("PRJ-99201", "CMP-2025"))
                .thenReturn(Optional.of(baselineDoc));

        List<AnomalyReportDTO> anomalies = anomalyDetector.detectScoreDropAnomalies("PRJ-99201", "CMP-2026", "CMP-2025");

        assertNotNull(anomalies);
        assertEquals(1, anomalies.size());
        assertEquals("WARNING", anomalies.get(0).getSeverity(), "Drop of 12 points (10-15) must trigger WARNING severity");
    }
}
