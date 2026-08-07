package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.cache.AnalyticsCacheService;
import com.talnova.tesp.analyticsservice.domain.model.AnalyticalSnapshotDocument;
import com.talnova.tesp.analyticsservice.event.CampaignClosedEvent;
import com.talnova.tesp.analyticsservice.repository.AnalyticalSnapshotRepository;
import com.talnova.tesp.analyticsservice.service.CampaignSnapshotFreezerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignSnapshotFreezerServiceTest {

    @Mock
    private AnalyticalSnapshotRepository snapshotRepository;

    @Mock
    private AnalyticsCacheService cacheService;

    private CampaignSnapshotFreezerServiceImpl freezerService;

    @BeforeEach
    void setUp() {
        freezerService = new CampaignSnapshotFreezerServiceImpl(snapshotRepository, cacheService);
    }

    @Test
    @DisplayName("TC-ANL-601-01: Freeze analytical snapshot on campaign closure event and invalidate Redis cache")
    void testFreezeCampaignSnapshot() {
        Instant now = Instant.now();
        CampaignClosedEvent event = new CampaignClosedEvent("CMP-1001", "PRJ-99201", now, 450);

        AnalyticalSnapshotDocument mockDoc = AnalyticalSnapshotDocument.builder()
                .id("SNAP-001")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .snapshotTimestamp(now)
                .totalResponses(450)
                .isFrozen(true)
                .build();

        when(snapshotRepository.save(any())).thenReturn(mockDoc);

        AnalyticalSnapshotDocument result = freezerService.freezeCampaignSnapshot(event);

        assertNotNull(result);
        assertEquals("CMP-1001", result.getCampaignId());
        assertTrue(result.getIsFrozen(), "Frozen snapshot document must have isFrozen = true");
        verify(snapshotRepository, times(1)).save(any());
        verify(cacheService, times(1)).invalidateCampaignCacheThrottled("PRJ-99201", "CMP-1001");
    }
}
