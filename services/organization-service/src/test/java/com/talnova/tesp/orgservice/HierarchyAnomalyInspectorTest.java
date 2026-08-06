package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.dto.HierarchyAnomalyReportDTO;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import com.talnova.tesp.orgservice.service.HierarchyAnomalyInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HierarchyAnomalyInspectorTest {

    @Mock
    private OrgNodeRepository repository;

    private HierarchyAnomalyInspector anomalyInspector;

    @BeforeEach
    void setUp() {
        anomalyInspector = new HierarchyAnomalyInspector(repository);
    }

    @Test
    @DisplayName("TC-ORG-601-A: Scanning tree with depth 22 flags EXTREME_DEPTH_WARNING anomaly")
    void testExtremeDepthAnomalyFlagged() {
        OrgNodeDocument deepNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-999")
                .name("Deep Sub-Team")
                .type("TEAM")
                .parentId("N-998")
                .path(",N-001,...N-999,")
                .depth(22)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        OrgNodeDocument rootNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-001")
                .name("Company")
                .type("COMPANY")
                .depth(1)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveSubTreeByPathPrefix("PRJ-99201", ","))
                .thenReturn(List.of(rootNode, deepNode));

        HierarchyAnomalyReportDTO report = anomalyInspector.inspectAnomalies("PRJ-99201");

        assertNotNull(report);
        assertEquals("PRJ-99201", report.getProjectId());
        assertEquals(2, report.getTotalNodesInspected());
        assertTrue(report.getTotalAnomaliesDetected() >= 1);

        HierarchyAnomalyReportDTO.AnomalyDetail anomaly = report.getAnomalies().stream()
                .filter(a -> "EXTREME_DEPTH_WARNING".equals(a.getAnomalyType()))
                .findFirst()
                .orElse(null);

        assertNotNull(anomaly);
        assertEquals("N-999", anomaly.getNodeId());
        assertEquals("HIGH", anomaly.getSeverity());
        assertTrue(anomaly.getRecommendation().contains("flattening organizational structure"));
    }

    @Test
    @DisplayName("TC-ORG-601-B: Scanning tree with orphan node flags ORPHAN_SUBTREE_WARNING anomaly")
    void testOrphanSubTreeAnomalyFlagged() {
        OrgNodeDocument orphanNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Orphaned Team")
                .type("TEAM")
                .parentId("N-DELETED-99")
                .path(",N-DELETED-99,N-201,")
                .depth(2)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveSubTreeByPathPrefix("PRJ-99201", ","))
                .thenReturn(List.of(orphanNode));

        HierarchyAnomalyReportDTO report = anomalyInspector.inspectAnomalies("PRJ-99201");

        assertNotNull(report);
        assertEquals(1, report.getTotalAnomaliesDetected());

        HierarchyAnomalyReportDTO.AnomalyDetail orphanAnomaly = report.getAnomalies().get(0);
        assertEquals("ORPHAN_SUBTREE_WARNING", orphanAnomaly.getAnomalyType());
        assertEquals("N-201", orphanAnomaly.getNodeId());
        assertEquals("CRITICAL", orphanAnomaly.getSeverity());
        assertTrue(orphanAnomaly.getRecommendation().contains("Re-parent orphan node"));
    }
}
