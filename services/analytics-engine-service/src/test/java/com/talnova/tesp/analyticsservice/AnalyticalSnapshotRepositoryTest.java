package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.domain.model.AnalyticalSnapshotDocument;
import com.talnova.tesp.analyticsservice.domain.model.GroupScore;
import com.talnova.tesp.analyticsservice.domain.model.NodeAggregate;
import com.talnova.tesp.analyticsservice.domain.model.NodeAggregateStatus;
import com.talnova.tesp.analyticsservice.repository.AnalyticalSnapshotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticalSnapshotRepositoryTest {

    @Test
    @DisplayName("TC-ANL-101-01: Verify AnalyticalSnapshotDocument model structure & privacy status enums")
    void testAnalyticalSnapshotDocumentModel() {
        NodeAggregate validNode = NodeAggregate.builder()
                .nodeId("N-101")
                .nodePath(",N-100,N-101,")
                .responseCount(45)
                .status(NodeAggregateStatus.VALID)
                .enps(42.0)
                .engagementIndex(78.4)
                .build();

        NodeAggregate suppressedNode = NodeAggregate.builder()
                .nodeId("N-999")
                .nodePath(",N-100,N-999,")
                .responseCount(3)
                .status(NodeAggregateStatus.SUPPRESSED)
                .enps(null)
                .engagementIndex(null)
                .build();

        AnalyticalSnapshotDocument document = AnalyticalSnapshotDocument.builder()
                .id("SNP-1001")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .snapshotDate(Instant.now())
                .totalResponses(48)
                .overallEnps(42.0)
                .overallEngagementIndex(78.4)
                .participationRate(85.5)
                .groupScores(List.of(
                        GroupScore.builder().groupId("GRP-LEADERSHIP").score(81.2).build()
                ))
                .nodeAggregates(List.of(validNode, suppressedNode))
                .createdAt(Instant.now())
                .build();

        assertNotNull(document);
        assertEquals("SNP-1001", document.getId());
        assertEquals("PRJ-99201", document.getProjectId());
        assertEquals("CMP-1001", document.getCampaignId());
        assertEquals(2, document.getNodeAggregates().size());

        assertEquals(NodeAggregateStatus.VALID, document.getNodeAggregates().get(0).getStatus());
        assertEquals(NodeAggregateStatus.SUPPRESSED, document.getNodeAggregates().get(1).getStatus());
        assertNull(document.getNodeAggregates().get(1).getEnps(), "Suppressed node must have null eNPS per BR-ANL-001");
    }
}
