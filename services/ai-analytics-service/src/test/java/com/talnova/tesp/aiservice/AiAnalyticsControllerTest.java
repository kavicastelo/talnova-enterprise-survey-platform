package com.talnova.tesp.aiservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.aiservice.controller.AiAnalyticsController;
import com.talnova.tesp.aiservice.domain.model.AiInsightDocument;
import com.talnova.tesp.aiservice.domain.model.SentimentLabel;
import com.talnova.tesp.aiservice.dto.ExecutiveSummaryDTO;
import com.talnova.tesp.aiservice.dto.SentimentOverrideDTO;
import com.talnova.tesp.aiservice.repository.AiInsightRepository;
import com.talnova.tesp.aiservice.summary.ExecutiveSummaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiAnalyticsController.class)
class AiAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiInsightRepository aiInsightRepository;

    @MockBean
    private ExecutiveSummaryService executiveSummaryService;

    @Test
    @DisplayName("TC-AI-701-01: PUT /api/v1/ai/insights/{insightId}/override sentiment re-classification per FR-AI-007 & BR-AI-003")
    void testOverrideSentimentTag() throws Exception {
        AiInsightDocument existing = AiInsightDocument.builder()
                .id("INSIGHT-100")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .sentimentLabel(SentimentLabel.NEGATIVE)
                .sentimentScore(-0.6)
                .build();

        when(aiInsightRepository.findById("INSIGHT-100")).thenReturn(Optional.of(existing));
        when(aiInsightRepository.save(any(AiInsightDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SentimentOverrideDTO overrideDTO = SentimentOverrideDTO.builder()
                .overriddenBy("USR-HR-001")
                .newLabel(SentimentLabel.NEUTRAL)
                .reason("Constructive critique context")
                .build();

        mockMvc.perform(put("/api/v1/ai/insights/INSIGHT-100/override")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overrideDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sentimentLabel").value("NEUTRAL"))
                .andExpect(jsonPath("$.humanOverride.overriddenBy").value("USR-HR-001"))
                .andExpect(jsonPath("$.humanOverride.originalLabel").value("NEGATIVE"));
    }

    @Test
    @DisplayName("TC-AI-701-02: GET /api/v1/ai/insights/projects/{projectId}/campaigns/{campaignId}")
    void testGetInsightsForCampaign() throws Exception {
        AiInsightDocument doc = AiInsightDocument.builder()
                .id("INSIGHT-101")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .sentimentLabel(SentimentLabel.POSITIVE)
                .build();

        when(aiInsightRepository.findByProjectIdAndCampaignId("PRJ-99201", "CMP-1001")).thenReturn(List.of(doc));

        mockMvc.perform(get("/api/v1/ai/insights/projects/PRJ-99201/campaigns/CMP-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("INSIGHT-101"))
                .andExpect(jsonPath("$[0].sentimentLabel").value("POSITIVE"));
    }

    @Test
    @DisplayName("TC-AI-701-03: POST /api/v1/ai/summary generate executive summary endpoint")
    void testGenerateExecutiveSummaryEndpoint() throws Exception {
        ExecutiveSummaryDTO summaryDTO = ExecutiveSummaryDTO.builder()
                .nodeScope("IT_DIVISION")
                .summaryTitle("AI Executive Summary — IT_DIVISION")
                .topStrengths(List.of("Strength 1", "Strength 2", "Strength 3"))
                .topConcerns(List.of("Concern 1", "Concern 2", "Concern 3"))
                .recommendations(List.of("Rec 1", "Rec 2"))
                .generatedAt(Instant.now())
                .build();

        when(executiveSummaryService.generateNodeExecutiveSummary(eq("IT_DIVISION"), any(), eq("OPENAI"))).thenReturn(summaryDTO);

        mockMvc.perform(post("/api/v1/ai/summary?nodeScope=IT_DIVISION&providerName=OPENAI")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Good experience\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeScope").value("IT_DIVISION"))
                .andExpect(jsonPath("$.topStrengths.length()").value(3));
    }
}
