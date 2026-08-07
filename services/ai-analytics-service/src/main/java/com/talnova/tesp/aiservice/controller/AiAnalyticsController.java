package com.talnova.tesp.aiservice.controller;

import com.talnova.tesp.aiservice.domain.model.AiInsightDocument;
import com.talnova.tesp.aiservice.domain.model.HumanOverride;
import com.talnova.tesp.aiservice.dto.ExecutiveSummaryDTO;
import com.talnova.tesp.aiservice.dto.SentimentOverrideDTO;
import com.talnova.tesp.aiservice.repository.AiInsightRepository;
import com.talnova.tesp.aiservice.summary.ExecutiveSummaryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
public class AiAnalyticsController {

    private static final Logger log = LoggerFactory.getLogger(AiAnalyticsController.class);

    private final AiInsightRepository aiInsightRepository;
    private final ExecutiveSummaryService executiveSummaryService;

    public AiAnalyticsController(AiInsightRepository aiInsightRepository, ExecutiveSummaryService executiveSummaryService) {
        this.aiInsightRepository = aiInsightRepository;
        this.executiveSummaryService = executiveSummaryService;
    }

    @GetMapping("/insights/projects/{projectId}/campaigns/{campaignId}")
    public ResponseEntity<List<AiInsightDocument>> getInsightsForCampaign(
            @PathVariable String projectId,
            @PathVariable String campaignId) {
        log.info("Fetching AI insights for projectId '{}' and campaignId '{}'", projectId, campaignId);
        List<AiInsightDocument> insights = aiInsightRepository.findByProjectIdAndCampaignId(projectId, campaignId);
        return ResponseEntity.ok(insights);
    }

    @PutMapping("/insights/{insightId}/override")
    public ResponseEntity<AiInsightDocument> overrideSentimentTag(
            @PathVariable String insightId,
            @Valid @RequestBody SentimentOverrideDTO overrideDTO) {
        log.info("Applying human sentiment override for insightId '{}' by '{}' per FR-AI-007 and BR-AI-003",
                insightId, overrideDTO.getOverriddenBy());

        AiInsightDocument document = aiInsightRepository.findById(insightId)
                .orElseThrow(() -> new IllegalArgumentException("AiInsightDocument not found for id: " + insightId));

        HumanOverride override = HumanOverride.builder()
                .overriddenBy(overrideDTO.getOverriddenBy())
                .originalLabel(document.getSentimentLabel() != null ? document.getSentimentLabel().name() : "NEUTRAL")
                .newLabel(overrideDTO.getNewLabel().name())
                .reason(overrideDTO.getReason())
                .overriddenAt(Instant.now())
                .build();

        document.setSentimentLabel(overrideDTO.getNewLabel());
        document.setHumanOverride(override);

        AiInsightDocument updated = aiInsightRepository.save(document);
        log.info("Successfully updated human override for insightId '{}'", insightId);

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/summaries")
    public ResponseEntity<ExecutiveSummaryDTO> getExecutiveSummary(
            @RequestParam(value = "campaignId", required = false) String campaignId,
            @RequestParam(value = "nodeId", required = false, defaultValue = "GLOBAL") String nodeId,
            @RequestParam(value = "providerName", required = false, defaultValue = "OPENAI") String providerName) {
        log.info("Fetching executive summary via GET for campaignId '{}', nodeId '{}' using provider '{}' per FR-AI-006",
                campaignId, nodeId, providerName);
        ExecutiveSummaryDTO summary = executiveSummaryService.generateNodeExecutiveSummary(nodeId, null, providerName);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/summary")
    public ResponseEntity<ExecutiveSummaryDTO> generateExecutiveSummary(
            @RequestParam(required = false, defaultValue = "GLOBAL") String nodeScope,
            @RequestParam(required = false, defaultValue = "OPENAI") String providerName,
            @RequestBody(required = false) List<String> rawComments) {
        log.info("Generating executive summary for nodeScope '{}' using provider '{}' per FR-AI-006", nodeScope, providerName);
        ExecutiveSummaryDTO summary = executiveSummaryService.generateNodeExecutiveSummary(nodeScope, rawComments, providerName);
        return ResponseEntity.ok(summary);
    }
}
