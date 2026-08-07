package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.cache.AnalyticsCacheService;
import com.talnova.tesp.analyticsservice.controller.AnalyticsController;
import com.talnova.tesp.analyticsservice.dto.DashboardMetricsDTO;
import com.talnova.tesp.analyticsservice.exception.AnalyticsExceptionHandler;
import com.talnova.tesp.analyticsservice.exception.UnauthorizedNodeScopeException;
import com.talnova.tesp.analyticsservice.filter.DemographicFilterCompilerService;
import com.talnova.tesp.analyticsservice.security.NodeScopeAbacFilterService;
import com.talnova.tesp.analyticsservice.service.HeatmapAggregatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnalyticsController.class)
@ContextConfiguration(classes = {AnalyticsController.class, AnalyticsExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsSecurityHeaderAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HeatmapAggregatorService heatmapAggregatorService;

    @MockBean
    private NodeScopeAbacFilterService abacFilterService;

    @MockBean
    private DemographicFilterCompilerService filterCompilerService;

    @MockBean
    private AnalyticsCacheService cacheService;

    @Test
    @DisplayName("X-Project-ID Header Resolution: GET /api/v1/analytics/dashboard resolves projectId from gateway header")
    void testHeaderResolutionGetDashboard() throws Exception {
        DashboardMetricsDTO mockMetrics = DashboardMetricsDTO.builder()
                .campaignId("CMP-1001")
                .nodeId("N-201")
                .totalResponses(450)
                .participationRate(82.5)
                .eNPS(42.0)
                .engagementIndex(78.4)
                .groupScores(Collections.emptyList())
                .build();

        when(filterCompilerService.compileFilters(anyMap())).thenReturn(Collections.emptyMap());
        when(heatmapAggregatorService.getDashboardMetrics(eq("PRJ-99201"), eq("CMP-1001"), eq("N-201"), anyMap()))
                .thenReturn(mockMetrics);

        mockMvc.perform(get("/api/v1/analytics/dashboard")
                        .header("X-Project-ID", "PRJ-99201")
                        .param("campaignId", "CMP-1001")
                        .param("nodeId", "N-201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.campaignId").value("CMP-1001"))
                .andExpect(jsonPath("$.data.eNPS").value(42.0));
    }

    @Test
    @DisplayName("BR-ANL-004 / VR-ANL-002: Unauthorized nodeScope access attempt returns 403 Forbidden")
    void testAbacNodeScopeViolationForbidden() throws Exception {
        doThrow(new UnauthorizedNodeScopeException("User node scope ',N-100,' does not authorize access to requested node 'N-999'"))
                .when(abacFilterService).validateNodeScopeAccess(",N-100,", "N-999");

        mockMvc.perform(get("/api/v1/analytics/dashboard")
                        .header("X-User-NodeScope", ",N-100,")
                        .param("campaignId", "CMP-1001")
                        .param("nodeId", "N-999"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.correlationId").value("ERR-ANL-403"));
    }

    @Test
    @DisplayName("TC-ANL-002 / BR-ANL-001: Small sample size (N < 5) suppresses numeric scores")
    void testSmallSampleSizeSuppressionGuard() throws Exception {
        DashboardMetricsDTO suppressedMetrics = DashboardMetricsDTO.builder()
                .campaignId("CMP-1001")
                .nodeId("N-SMALL-TEAM")
                .totalResponses(4)
                .participationRate(30.0)
                .eNPS(null)
                .engagementIndex(null)
                .groupScores(Collections.emptyList())
                .build();

        when(filterCompilerService.compileFilters(anyMap())).thenReturn(Collections.emptyMap());
        when(heatmapAggregatorService.getDashboardMetrics(eq("PRJ-99201"), eq("CMP-1001"), eq("N-SMALL-TEAM"), anyMap()))
                .thenReturn(suppressedMetrics);

        mockMvc.perform(get("/api/v1/analytics/dashboard")
                        .header("X-Project-ID", "PRJ-99201")
                        .param("campaignId", "CMP-1001")
                        .param("nodeId", "N-SMALL-TEAM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalResponses").value(4))
                .andExpect(jsonPath("$.data.eNPS").doesNotExist());
    }
}
