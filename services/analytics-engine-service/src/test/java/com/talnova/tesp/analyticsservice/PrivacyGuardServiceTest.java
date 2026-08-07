package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.domain.model.NodeAggregate;
import com.talnova.tesp.analyticsservice.domain.model.NodeAggregateStatus;
import com.talnova.tesp.analyticsservice.dto.HeatmapCellDTO;
import com.talnova.tesp.analyticsservice.privacy.PrivacyGuardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrivacyGuardServiceTest {

    private PrivacyGuardServiceImpl privacyGuard;

    @BeforeEach
    void setUp() {
        privacyGuard = new PrivacyGuardServiceImpl();
    }

    @Test
    @DisplayName("TC-ANL-002 / TC-ANL-501-01: Cohort with N = 4 < 5 suppresses numeric scores and sets status = SUPPRESSED")
    void testSanitizeNodeAggregateSuppressed() {
        NodeAggregate raw = NodeAggregate.builder()
                .nodeId("N-999")
                .nodePath(",N-100,N-999,")
                .responseCount(4)
                .status(NodeAggregateStatus.VALID)
                .enps(35.0)
                .engagementIndex(72.0)
                .build();

        NodeAggregate sanitized = privacyGuard.sanitizeNodeAggregate(raw);

        assertNotNull(sanitized);
        assertEquals(NodeAggregateStatus.SUPPRESSED, sanitized.getStatus());
        assertNull(sanitized.getEnps(), "eNPS score must be suppressed (null) when N < 5 per BR-ANL-001");
        assertNull(sanitized.getEngagementIndex(), "Engagement index must be suppressed (null) when N < 5 per BR-ANL-001");
    }

    @Test
    @DisplayName("TC-ANL-501-02: Cohort with N = 5 >= 5 retains valid numeric scores and status = VALID")
    void testSanitizeNodeAggregateValid() {
        NodeAggregate raw = NodeAggregate.builder()
                .nodeId("N-201")
                .nodePath(",N-100,N-201,")
                .responseCount(5)
                .status(NodeAggregateStatus.VALID)
                .enps(40.0)
                .engagementIndex(76.5)
                .build();

        NodeAggregate sanitized = privacyGuard.sanitizeNodeAggregate(raw);

        assertNotNull(sanitized);
        assertEquals(NodeAggregateStatus.VALID, sanitized.getStatus());
        assertEquals(40.0, sanitized.getEnps());
        assertEquals(76.5, sanitized.getEngagementIndex());
    }

    @Test
    @DisplayName("TC-ANL-501-03: Heatmap cell with N = 3 < 5 sets score = null and colorIntensity = GREY")
    void testSanitizeHeatmapCellSuppressed() {
        HeatmapCellDTO rawCell = HeatmapCellDTO.builder()
                .nodeId("N-999")
                .groupId("GRP-CULTURE")
                .sampleSize(3)
                .score(80.0)
                .colorIntensity("GREEN")
                .build();

        HeatmapCellDTO sanitized = privacyGuard.sanitizeHeatmapCell(rawCell);

        assertNotNull(sanitized);
        assertNull(sanitized.getScore());
        assertEquals("GREY", sanitized.getColorIntensity());
    }
}
