package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.cache.AnalyticsCacheService;
import com.talnova.tesp.analyticsservice.controller.AnalyticsController;
import com.talnova.tesp.analyticsservice.dto.DashboardMetricsDTO;
import com.talnova.tesp.analyticsservice.dto.HeatmapMatrixDTO;
import com.talnova.tesp.analyticsservice.exception.AnalyticsExceptionHandler;
import com.talnova.tesp.analyticsservice.filter.DemographicFilterCompilerService;
import com.talnova.tesp.analyticsservice.security.NodeScopeAbacFilterService;
import com.talnova.tesp.analyticsservice.service.HeatmapAggregatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HeatmapAggregatorService heatmapAggregatorService;

    @Mock
    private NodeScopeAbacFilterService abacFilterService;

    @Mock
    private DemographicFilterCompilerService filterCompilerService;

    @Mock
    private AnalyticsCacheService cacheService;

    @BeforeEach
    void setUp() {
        AnalyticsController controller = new AnalyticsController(
                heatmapAggregatorService,
                abacFilterService,
                filterCompilerService,
                cacheService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new AnalyticsExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-ANL-502-01: GET /api/v1/analytics/dashboard returns 200 OK with dashboard metrics")
    void testGetDashboardMetricsSuccess() throws Exception {
        DashboardMetricsDTO mockDto = DashboardMetricsDTO.builder()
                .campaignId("CMP-1001")
                .nodeId("N-201")
                .totalResponses(450)
                .participationRate(82.5)
                .eNPS(42.0)
                .engagementIndex(78.4)
                .groupScores(List.of())
                .build();

        when(filterCompilerService.compileFilters(any())).thenReturn(Map.of());
        when(heatmapAggregatorService.getDashboardMetrics(eq("PRJ-99201"), eq("CMP-1001"), eq("N-201"), any()))
                .thenReturn(mockDto);

        mockMvc.perform(get("/api/v1/analytics/dashboard")
                        .header("X-Project-ID", "PRJ-99201")
                        .param("campaignId", "CMP-1001")
                        .param("nodeId", "N-201")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.campaignId").value("CMP-1001"))
                .andExpect(jsonPath("$.data.eNPS").value(42.0));
    }

    @Test
    @DisplayName("TC-ANL-502-02: GET /api/v1/analytics/heatmap returns 200 OK with 2D heatmap matrix")
    void testGetHeatmapMatrixSuccess() throws Exception {
        HeatmapMatrixDTO mockMatrix = HeatmapMatrixDTO.builder()
                .campaignId("CMP-1001")
                .parentNodeId("N-100")
                .rowNodes(List.of("N-101"))
                .columnThemes(List.of("GRP-LEADERSHIP"))
                .cells(List.of())
                .build();

        when(filterCompilerService.compileFilters(any())).thenReturn(Map.of());
        when(heatmapAggregatorService.computeHeatmapMatrix(eq("PRJ-99201"), eq("CMP-1001"), eq("N-100"), any()))
                .thenReturn(mockMatrix);

        mockMvc.perform(get("/api/v1/analytics/heatmap")
                        .header("X-Project-ID", "PRJ-99201")
                        .param("campaignId", "CMP-1001")
                        .param("parentNodeId", "N-100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.parentNodeId").value("N-100"));
    }
}
