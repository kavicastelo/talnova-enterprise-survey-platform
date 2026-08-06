package com.talnova.tesp.orgservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.orgservice.controller.OrgNodeController;
import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.dto.CreateNodeDTO;
import com.talnova.tesp.orgservice.dto.HierarchyAnomalyReportDTO;
import com.talnova.tesp.orgservice.dto.OrgNodeResponseDTO;
import com.talnova.tesp.orgservice.exception.DuplicateNodeIdException;
import com.talnova.tesp.orgservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.orgservice.service.HierarchyAnomalyInspector;
import com.talnova.tesp.orgservice.service.OrgNodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrgNodeController.class)
@ContextConfiguration(classes = {OrgNodeController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class OrgNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrgNodeService nodeService;

    @MockBean
    private HierarchyAnomalyInspector anomalyInspector;

    private CreateNodeDTO createDTO;
    private OrgNodeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        createDTO = CreateNodeDTO.builder()
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Engineering Division")
                .type("DIVISION")
                .parentId("N-101")
                .displayOrder(1)
                .attributes(Map.of("CostCenter", "CC-9920"))
                .build();

        responseDTO = OrgNodeResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b9999")
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Engineering Division")
                .type("DIVISION")
                .parentId("N-101")
                .path(",N-001,N-101,N-201,")
                .depth(3)
                .displayOrder(1)
                .status(NodeStatus.ACTIVE)
                .attributes(Map.of("CostCenter", "CC-9920"))
                .build();
    }

    @Test
    @DisplayName("TC-ORG-202-A: POST /api/v1/nodes creates node and returns 201 Created with Location header")
    void testCreateNodeSuccess() throws Exception {
        when(nodeService.createNode(any(CreateNodeDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/nodes/N-201"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nodeId").value("N-201"))
                .andExpect(jsonPath("$.data.path").value(",N-001,N-101,N-201,"))
                .andExpect(jsonPath("$.data.depth").value(3));
    }

    @Test
    @DisplayName("TC-ORG-202-B: GET /api/v1/nodes/N-201 returns node details")
    void testGetNodeSuccess() throws Exception {
        when(nodeService.getNodeByProjectIdAndNodeId("PRJ-99201", "N-201")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/nodes/N-201")
                        .param("projectId", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nodeId").value("N-201"))
                .andExpect(jsonPath("$.data.path").value(",N-001,N-101,N-201,"));
    }

    @Test
    @DisplayName("TC-ORG-202-C: Duplicate nodeId creation returns 400 Bad Request RFC 7807")
    void testDuplicateNodeIdReturnsBadRequest() throws Exception {
        when(nodeService.createNode(any(CreateNodeDTO.class))).thenThrow(new DuplicateNodeIdException("N-201"));

        mockMvc.perform(post("/api/v1/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Duplicate Node Identifier"));
    }

    @Test
    @DisplayName("TC-ORG-601-A: GET /api/v1/nodes/anomalies returns AI hierarchy anomaly report")
    void testInspectAnomaliesApi() throws Exception {
        HierarchyAnomalyReportDTO report = HierarchyAnomalyReportDTO.builder()
                .projectId("PRJ-99201")
                .totalNodesInspected(25)
                .totalAnomaliesDetected(1)
                .anomalies(List.of(HierarchyAnomalyReportDTO.AnomalyDetail.builder()
                        .anomalyType("EXTREME_DEPTH_WARNING")
                        .nodeId("N-999")
                        .severity("HIGH")
                        .message("Extreme depth level 22")
                        .recommendation("Flatten tree structure")
                        .build()))
                .build();

        when(anomalyInspector.inspectAnomalies("PRJ-99201")).thenReturn(report);

        mockMvc.perform(get("/api/v1/nodes/anomalies")
                        .param("projectId", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalNodesInspected").value(25))
                .andExpect(jsonPath("$.data.totalAnomaliesDetected").value(1))
                .andExpect(jsonPath("$.data.anomalies[0].anomalyType").value("EXTREME_DEPTH_WARNING"));
    }
}
