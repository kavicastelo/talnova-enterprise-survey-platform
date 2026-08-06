package com.talnova.tesp.distservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.distservice.controller.TokenManagementController;
import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationRequestDTO;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationResponseDTO;
import com.talnova.tesp.distservice.dto.TokenCachePayloadDTO;
import com.talnova.tesp.distservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.distservice.service.CryptographicTokenGenerator;
import com.talnova.tesp.distservice.service.RedisTokenCacheService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TokenManagementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CryptographicTokenGenerator tokenGenerator;

    @Mock
    private RedisTokenCacheService tokenCacheService;

    @InjectMocks
    private TokenManagementController tokenManagementController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(tokenManagementController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-DST-201-04: POST /api/v1/tokens/generate returns 201 Created with generated tokens payload")
    void testGenerateTokensEndpoint() throws Exception {
        TokenBatchGenerationRequestDTO requestDTO = TokenBatchGenerationRequestDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .employeeIds(List.of("EMP-10020", "EMP-10021"))
                .campaignSalt("SALT-TEST")
                .build();

        GeneratedTokenDTO token1 = GeneratedTokenDTO.builder()
                .token("TOKEN-ABC-123")
                .employeeId("EMP-10020")
                .campaignId("CMP-1001")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .build();

        TokenBatchGenerationResponseDTO responseDTO = TokenBatchGenerationResponseDTO.builder()
                .campaignId("CMP-1001")
                .totalGenerated(1)
                .tokens(List.of(token1))
                .generationDurationMs(12)
                .build();

        when(tokenGenerator.generateTokensForCampaign(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/tokens/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.campaignId").value("CMP-1001"))
                .andExpect(jsonPath("$.data.totalGenerated").value(1))
                .andExpect(jsonPath("$.data.tokens[0].token").value("TOKEN-ABC-123"));
    }

    @Test
    @DisplayName("TC-DST-302-03: POST /api/v1/tokens/burn returns 200 OK on valid token burn")
    void testBurnTokenEndpointSuccess() throws Exception {
        TokenCachePayloadDTO payload = TokenCachePayloadDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .build();

        when(tokenCacheService.validateAndBurnToken("TOKEN-ABC-123")).thenReturn(Optional.of(payload));

        mockMvc.perform(post("/api/v1/tokens/burn")
                        .param("token", "TOKEN-ABC-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.campaignId").value("CMP-1001"));
    }

    @Test
    @DisplayName("TC-DST-302-04: POST /api/v1/tokens/burn returns 400 Bad Request on already burned token")
    void testBurnTokenEndpointFailure() throws Exception {
        when(tokenCacheService.validateAndBurnToken("TOKEN-BURNED-999")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/tokens/burn")
                        .param("token", "TOKEN-BURNED-999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("BR-DST-001 / BR-DST-003: Token is invalid, expired, or already burned"));
    }
}
