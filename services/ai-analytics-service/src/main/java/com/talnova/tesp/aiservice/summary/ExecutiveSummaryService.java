package com.talnova.tesp.aiservice.summary;

import com.talnova.tesp.aiservice.dto.ExecutiveSummaryDTO;

import java.util.List;

public interface ExecutiveSummaryService {

    /**
     * Compiles sanitized text comments for an Organization Node and generates a structured executive summary
     * (3 strengths, 3 concerns, 2 recommendations) per FR-AI-006 and US-AI-004.
     */
    ExecutiveSummaryDTO generateNodeExecutiveSummary(String nodeScope, List<String> sanitizedComments, String providerName);
}
