package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.controller.CampaignController;
import com.talnova.tesp.distservice.controller.TokenManagementController;
import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.CampaignResponseDTO;
import com.talnova.tesp.distservice.dto.TokenCachePayloadDTO;
import com.talnova.tesp.distservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.distservice.service.CampaignService;
import com.talnova.tesp.distservice.service.CryptographicTokenGenerator;
import com.talnova.tesp.distservice.service.RedisTokenCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CampaignController.class, TokenManagementController.class})
@ContextConfiguration(classes = {CampaignController.class, TokenManagementController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class CampaignSecurityHeaderAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampaignService campaignService;

    @MockBean
    private CryptographicTokenGenerator tokenGenerator;

    @MockBean
    private RedisTokenCacheService tokenCacheService;

    @Test
    @DisplayName("X-Project-ID Header Fallback: GET /api/v1/campaigns/CMP-1001 retrieves campaign details without query parameter")
    void testHeaderFallbackGetCampaign() throws Exception {
        CampaignResponseDTO mockResponse = CampaignResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b1111")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .title("Q3 Pulse Survey")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .channels(List.of(DistributionChannel.EMAIL))
                .status(CampaignStatus.ACTIVE)
                .startDate(Instant.now())
                .expirationDate(Instant.now().plusSeconds(86400))
                .build();

        when(campaignService.getCampaign("PRJ-99201", "CMP-1001")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/campaigns/CMP-1001")
                        .header("X-Project-ID", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.campaignId").value("CMP-1001"));
    }

    @Test
    @DisplayName("BR-DST-001: Token burn endpoint invalidates single-use token and returns token metadata payload")
    void testSingleUseTokenBurnSuccess() throws Exception {
        TokenCachePayloadDTO mockPayload = TokenCachePayloadDTO.builder()
                .campaignId("CMP-1001")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .build();

        when(tokenCacheService.validateAndBurnToken("TKN-VALID-99")).thenReturn(Optional.of(mockPayload));

        mockMvc.perform(post("/api/v1/tokens/burn")
                        .param("token", "TKN-VALID-99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.campaignId").value("CMP-1001"));
    }

    @Test
    @DisplayName("BR-DST-001 / BR-DST-003: Invalid or already burned token returns 400 Bad Request")
    void testInvalidTokenBurnRejection() throws Exception {
        when(tokenCacheService.validateAndBurnToken("TKN-INVALID-00")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/tokens/burn")
                        .param("token", "TKN-INVALID-00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("BR-DST-001 / BR-DST-003: Token is invalid, expired, or already burned"));
    }
}
