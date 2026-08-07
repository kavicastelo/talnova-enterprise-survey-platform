package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.domain.model.AnalyticalSnapshotDocument;
import com.talnova.tesp.analyticsservice.dto.LongitudinalDeltaDTO;
import com.talnova.tesp.analyticsservice.repository.AnalyticalSnapshotRepository;
import com.talnova.tesp.analyticsservice.service.LongitudinalAnalysisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LongitudinalAnalysisServiceTest {

    @Mock
    private AnalyticalSnapshotRepository snapshotRepository;

    private LongitudinalAnalysisServiceImpl longitudinalService;

    @BeforeEach
    void setUp() {
        longitudinalService = new LongitudinalAnalysisServiceImpl(snapshotRepository);
    }

    @Test
    @DisplayName("TC-ANL-602-01: Calculate positive longitudinal deltas and set trend direction = UP")
    void testCalculateLongitudinalDeltaPositive() {
        AnalyticalSnapshotDocument currentDoc = AnalyticalSnapshotDocument.builder()
                .campaignId("CMP-2026")
                .overallEnps(45.0)
                .overallEngagementIndex(80.0)
                .build();

        AnalyticalSnapshotDocument baselineDoc = AnalyticalSnapshotDocument.builder()
                .campaignId("CMP-2025")
                .overallEnps(30.0)
                .overallEngagementIndex(70.0)
                .build();

        when(snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc("PRJ-99201", "CMP-2026"))
                .thenReturn(Optional.of(currentDoc));
        when(snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc("PRJ-99201", "CMP-2025"))
                .thenReturn(Optional.of(baselineDoc));

        LongitudinalDeltaDTO result = longitudinalService.calculateLongitudinalDelta("PRJ-99201", "CMP-2026", "CMP-2025");

        assertNotNull(result);
        assertEquals(15.0, result.getEnpsDelta());
        assertEquals(10.0, result.getEngagementIndexDelta());
        assertEquals("UP", result.getTrendDirection());
    }

    @Test
    @DisplayName("TC-ANL-602-02: Calculate negative longitudinal deltas and set trend direction = DOWN")
    void testCalculateLongitudinalDeltaNegative() {
        AnalyticalSnapshotDocument currentDoc = AnalyticalSnapshotDocument.builder()
                .campaignId("CMP-2026")
                .overallEnps(20.0)
                .overallEngagementIndex(60.0)
                .build();

        AnalyticalSnapshotDocument baselineDoc = AnalyticalSnapshotDocument.builder()
                .campaignId("CMP-2025")
                .overallEnps(35.0)
                .overallEngagementIndex(75.0)
                .build();

        when(snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc("PRJ-99201", "CMP-2026"))
                .thenReturn(Optional.of(currentDoc));
        when(snapshotRepository.findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc("PRJ-99201", "CMP-2025"))
                .thenReturn(Optional.of(baselineDoc));

        LongitudinalDeltaDTO result = longitudinalService.calculateLongitudinalDelta("PRJ-99201", "CMP-2026", "CMP-2025");

        assertNotNull(result);
        assertEquals(-15.0, result.getEnpsDelta());
        assertEquals(-15.0, result.getEngagementIndexDelta());
        assertEquals("DOWN", result.getTrendDirection());
    }
}
