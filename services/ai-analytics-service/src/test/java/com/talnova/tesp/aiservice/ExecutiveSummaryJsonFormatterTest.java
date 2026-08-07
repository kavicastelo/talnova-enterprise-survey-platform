package com.talnova.tesp.aiservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.aiservice.dto.ExecutiveSummaryDTO;
import com.talnova.tesp.aiservice.summary.ExecutiveSummaryJsonFormatterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecutiveSummaryJsonFormatterTest {

    private ExecutiveSummaryJsonFormatterImpl formatter;

    @BeforeEach
    void setUp() {
        formatter = new ExecutiveSummaryJsonFormatterImpl(new ObjectMapper());
    }

    @Test
    @DisplayName("TC-AI-602-01: Parse markdown LLM completion into structured ExecutiveSummaryDTO per FR-AI-006")
    void testParseAndFormatMarkdownOutput() {
        String rawLlmOutput = """
                ### Strengths
                - Excellent cross-functional collaboration
                - Strong employee commitment
                - High satisfaction with training programs
                
                ### Concerns
                - Heavy workload during peak release cycles
                - Equipment maintenance delays in branch offices
                - Communication gaps from upper management
                
                ### Recommendations
                - Rebalance team workload allocation across sprints
                - Schedule bi-weekly feedback town halls
                """;

        ExecutiveSummaryDTO dto = formatter.parseAndFormat("FACTORY_BRANCH_B", rawLlmOutput);

        assertNotNull(dto);
        assertEquals("FACTORY_BRANCH_B", dto.getNodeScope());
        assertEquals(3, dto.getTopStrengths().size());
        assertEquals("Excellent cross-functional collaboration", dto.getTopStrengths().get(0));
        assertEquals(3, dto.getTopConcerns().size());
        assertEquals(2, dto.getRecommendations().size());
        assertNotNull(dto.getGeneratedAt());
    }
}
