package com.talnova.tesp.aiservice.summary;

import com.talnova.tesp.aiservice.dto.ExecutiveSummaryDTO;

public interface ExecutiveSummaryJsonFormatter {

    /**
     * Parses raw LLM text/markdown response into structured ExecutiveSummaryDTO per FR-AI-006.
     */
    ExecutiveSummaryDTO parseAndFormat(String nodeScope, String rawLlmOutput);
}
