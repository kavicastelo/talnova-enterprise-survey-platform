package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.event.AnalyticalSnapshotCreatedEvent;
import com.talnova.tesp.actionservice.generator.DraftActionPlanGeneratorService;
import com.talnova.tesp.actionservice.listener.AnalyticalSnapshotListener;
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
class AnalyticalSnapshotListenerTest {

    @Mock
    private DraftActionPlanGeneratorService generatorService;

    private AnalyticalSnapshotListener listener;

    @BeforeEach
    void setUp() {
        listener = new AnalyticalSnapshotListener(generatorService);
    }

    @Test
    @DisplayName("TC-ACT-201-01: Department with score 54.0% (< 60.0%) triggers draft action plan pipeline per FR-ACT-001")
    void testLowScoreTriggersDraftPipeline() {
        AnalyticalSnapshotCreatedEvent lowScoreEvent = AnalyticalSnapshotCreatedEvent.builder()
                .eventId("EVT-1001")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .nodeId("N-301")
                .groupId("GRP-COMMUNICATION")
                .categoryName("Leadership Communication")
                .score(54.0) // 54% < 60% threshold
                .timestamp(Instant.now())
                .build();

        boolean triggered = listener.processSnapshotEvent(lowScoreEvent);
        assertTrue(triggered, "Score 54.0% must trigger action plan creation pipeline");
        verify(generatorService, times(1)).generateDraftActionPlan(any(AnalyticalSnapshotCreatedEvent.class));
    }

    @Test
    @DisplayName("TC-ACT-201-02: Department with score 65.0% (>= 60.0%) does not trigger draft action plan pipeline")
    void testSatisfactoryScoreIgnored() {
        AnalyticalSnapshotCreatedEvent normalScoreEvent = AnalyticalSnapshotCreatedEvent.builder()
                .eventId("EVT-1002")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .nodeId("N-102")
                .groupId("GRP-CULTURE")
                .categoryName("Organizational Culture")
                .score(65.0) // 65% >= 60% threshold
                .timestamp(Instant.now())
                .build();

        boolean triggered = listener.processSnapshotEvent(normalScoreEvent);
        assertFalse(triggered, "Score 65.0% must NOT trigger action plan creation pipeline");
        verify(generatorService, never()).generateDraftActionPlan(any(AnalyticalSnapshotCreatedEvent.class));
    }
}
