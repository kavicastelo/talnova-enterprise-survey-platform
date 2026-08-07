package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.controller.AiAnalyticsController;
import com.talnova.tesp.aiservice.domain.model.AiInsightDocument;
import com.talnova.tesp.aiservice.domain.model.SentimentLabel;
import com.talnova.tesp.aiservice.dto.ExecutiveSummaryDTO;
import com.talnova.tesp.aiservice.dto.SentimentOverrideDTO;
import com.talnova.tesp.aiservice.exception.AiExceptionHandler;
import com.talnova.tesp.aiservice.pii.PiiSanitizerEngine;
import com.talnova.tesp.aiservice.pii.ZeroPiiInterceptor;
import com.talnova.tesp.aiservice.repository.AiInsightRepository;
import com.talnova.tesp.aiservice.summary.ExecutiveSummaryService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AiAnalyticsController.class)
@ContextConfiguration(classes = {AiAnalyticsController.class, AiExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class AiSecurityPiiAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiInsightRepository aiInsightRepository;

    @MockBean
    private ExecutiveSummaryService executiveSummaryService;

    @Test
    @DisplayName("TC-AI-001: PII Sanitizer engine masks names, emails, and phone numbers before LLM invocation")
    void testPiiSanitizerEngineMasking() {
        com.talnova.tesp.aiservice.pii.PiiSanitizerEngineImpl engine = new com.talnova.tesp.aiservice.pii.PiiSanitizerEngineImpl();
        String rawComment = "Contact John Doe at john.doe@aitkenspence.lk or +94771234567 regarding safety concerns.";

        String sanitized = engine.sanitizeText(rawComment);

        assertFalse(sanitized.contains("john.doe@aitkenspence.lk"), "Email must be masked");
        assertFalse(sanitized.contains("+94771234567"), "Phone number must be masked");
        assertTrue(sanitized.contains("[MASKED_EMAIL]"), "Masked email tag must be present");
        assertTrue(sanitized.contains("[MASKED_PHONE]"), "Masked phone tag must be present");
    }

    @Test
    @DisplayName("FR-AI-006: GET /api/v1/ai/summaries retrieves executive summary for node scope")
    void testGetExecutiveSummaryEndpoint() throws Exception {
        ExecutiveSummaryDTO mockSummary = ExecutiveSummaryDTO.builder()
                .nodeScope("N-301")
                .summaryTitle("Executive Summary for N-301")
                .topStrengths(List.of("Clear leadership direction", "Flexible hybrid work", "Good team culture"))
                .topConcerns(List.of("End-of-quarter audit workload", "Cross-department communication"))
                .recommendations(List.of("Resource allocation review", "Shared Slack channels"))
                .build();

        when(executiveSummaryService.generateNodeExecutiveSummary(eq("N-301"), any(), eq("OPENAI")))
                .thenReturn(mockSummary);

        mockMvc.perform(get("/api/v1/ai/summaries")
                        .param("campaignId", "CMP-1001")
                        .param("nodeId", "N-301")
                        .param("providerName", "OPENAI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeScope").value("N-301"))
                .andExpect(jsonPath("$.summaryTitle").value("Executive Summary for N-301"))
                .andExpect(jsonPath("$.topStrengths[0]").value("Clear leadership direction"));
    }

    @Test
    @DisplayName("FR-AI-007 / BR-AI-003: PUT /api/v1/ai/insights/{insightId}/override updates human sentiment override")
    void testHumanSentimentOverride() throws Exception {
        AiInsightDocument mockInsight = AiInsightDocument.builder()
                .id("INSIGHT-88")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .responseId("RSP-101")
                .questionId("Q-5")
                .sentimentScore(-0.4)
                .sentimentLabel(SentimentLabel.NEGATIVE)
                .build();

        when(aiInsightRepository.findById("INSIGHT-88")).thenReturn(Optional.of(mockInsight));
        when(aiInsightRepository.save(any(AiInsightDocument.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(put("/api/v1/ai/insights/INSIGHT-88/override")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newLabel\":\"NEUTRAL\",\"reason\":\"Constructive feedback\",\"overriddenBy\":\"USR-HR-001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INSIGHT-88"))
                .andExpect(jsonPath("$.sentimentLabel").value("NEUTRAL"))
                .andExpect(jsonPath("$.humanOverride.overriddenBy").value("USR-HR-001"));
    }
}
