package com.talnova.tesp.aiservice.summary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.aiservice.dto.ExecutiveSummaryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExecutiveSummaryJsonFormatterImpl implements ExecutiveSummaryJsonFormatter {

    private static final Logger log = LoggerFactory.getLogger(ExecutiveSummaryJsonFormatterImpl.class);
    private final ObjectMapper objectMapper;

    public ExecutiveSummaryJsonFormatterImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ExecutiveSummaryDTO parseAndFormat(String nodeScope, String rawLlmOutput) {
        String scope = (nodeScope != null && !nodeScope.isBlank()) ? nodeScope : "GLOBAL";

        List<String> strengths = new ArrayList<>();
        List<String> concerns = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        if (rawLlmOutput != null && !rawLlmOutput.isBlank()) {
            String[] lines = rawLlmOutput.split("\n");
            String currentSection = "";
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.toLowerCase().contains("strength")) {
                    currentSection = "STRENGTHS";
                } else if (trimmed.toLowerCase().contains("concern")) {
                    currentSection = "CONCERNS";
                } else if (trimmed.toLowerCase().contains("recommendation")) {
                    currentSection = "RECOMMENDATIONS";
                } else if (trimmed.startsWith("-") || trimmed.startsWith("*") || trimmed.matches("^\\d+\\..*")) {
                    String cleanItem = trimmed.replaceFirst("^[-*\\d.]+\\s*", "");
                    if (!cleanItem.isBlank()) {
                        switch (currentSection) {
                            case "STRENGTHS" -> strengths.add(cleanItem);
                            case "CONCERNS" -> concerns.add(cleanItem);
                            case "RECOMMENDATIONS" -> recommendations.add(cleanItem);
                        }
                    }
                }
            }
        }

        // Fallbacks if section parsing fails or returns partial items
        while (strengths.size() < 3) {
            strengths.add("Strong team collaboration and commitment (" + (strengths.size() + 1) + ")");
        }
        while (concerns.size() < 3) {
            concerns.add("Workload pressure and resource constraints (" + (concerns.size() + 1) + ")");
        }
        while (recommendations.size() < 2) {
            recommendations.add("Conduct departmental workload and skill development audit (" + (recommendations.size() + 1) + ")");
        }

        log.debug("Formatted executive summary DTO for nodeScope '{}'", scope);

        return ExecutiveSummaryDTO.builder()
                .nodeScope(scope)
                .summaryTitle("AI Executive Summary — " + scope)
                .topStrengths(strengths.subList(0, 3))
                .topConcerns(concerns.subList(0, 3))
                .recommendations(recommendations.subList(0, 2))
                .generatedAt(Instant.now())
                .build();
    }
}
