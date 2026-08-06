package com.talnova.tesp.distservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.distservice.controller.CampaignController;
import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.CampaignCreateDTO;
import com.talnova.tesp.distservice.dto.CampaignMetricsDTO;
import com.talnova.tesp.distservice.dto.CampaignResponseDTO;
import com.talnova.tesp.distservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.distservice.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CampaignControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController campaignController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(campaignController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-DST-102-05: POST /api/v1/campaigns returns 201 Created on valid payload")
    void testCreateCampaignEndpoint() throws Exception {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(86400 * 14);

        CampaignCreateDTO createDTO = CampaignCreateDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .title("Q3 Employee Pulse Survey")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .channels(List.of(DistributionChannel.EMAIL, DistributionChannel.TEAMS))
                .startDate(now)
                .expirationDate(expiry)
                .build();

        CampaignResponseDTO responseDTO = CampaignResponseDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .status(CampaignStatus.ACTIVE)
                .metrics(CampaignMetricsDTO.builder().totalTargeted(5000).build())
                .build();

        when(campaignService.createCampaign(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.campaignId").value("CMP-1001"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("TC-DST-102-06: GET /api/v1/campaigns/{campaignId} returns campaign details")
    void testGetCampaignEndpoint() throws Exception {
        CampaignResponseDTO responseDTO = CampaignResponseDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .status(CampaignStatus.ACTIVE)
                .build();

        when(campaignService.getCampaign("PRJ-99201", "CMP-1001")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/campaigns/CMP-1001")
                        .param("projectId", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.campaignId").value("CMP-1001"));
    }

    @Test
    @DisplayName("TC-DST-102-07: PATCH /api/v1/campaigns/{campaignId}/status updates campaign status")
    void testUpdateCampaignStatusEndpoint() throws Exception {
        CampaignResponseDTO responseDTO = CampaignResponseDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .status(CampaignStatus.PAUSED)
                .build();

        when(campaignService.updateCampaignStatus(eq("PRJ-99201"), eq("CMP-1001"), eq(CampaignStatus.PAUSED))).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/campaigns/CMP-1001/status")
                        .param("projectId", "PRJ-99201")
                        .param("status", "PAUSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PAUSED"));
    }
}
