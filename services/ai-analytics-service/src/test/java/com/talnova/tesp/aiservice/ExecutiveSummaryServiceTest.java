package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.adapter.AiAdapterFactoryServiceImpl;
import com.talnova.tesp.aiservice.adapter.OpenAiAdapter;
import com.talnova.tesp.aiservice.dto.ExecutiveSummaryDTO;
import com.talnova.tesp.aiservice.pii.PiiSanitizerEngineImpl;
import com.talnova.tesp.aiservice.pii.ZeroPiiInterceptor;
import com.talnova.tesp.aiservice.summary.ExecutiveSummaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecutiveSummaryServiceTest {

    private ExecutiveSummaryServiceImpl summaryService;

    @BeforeEach
    void setUp() {
        PiiSanitizerEngineImpl piiEngine = new PiiSanitizerEngineImpl();
        ZeroPiiInterceptor zeroPiiInterceptor = new ZeroPiiInterceptor(piiEngine);
        OpenAiAdapter openAiAdapter = new OpenAiAdapter(zeroPiiInterceptor);
        AiAdapterFactoryServiceImpl factoryService = new AiAdapterFactoryServiceImpl(List.of(openAiAdapter));

        summaryService = new ExecutiveSummaryServiceImpl(factoryService, piiEngine);
    }

    @Test
    @DisplayName("TC-AI-601-01: Compile departmental executive summary for IT Division per US-AI-004 & FR-AI-006")
    void testGenerateNodeExecutiveSummary() {
        List<String> rawComments = List.of(
                "Working with John Doe at john.doe@email.com was a great experience.",
                "We urgently need a workload audit due to high deadline pressure."
        );

        ExecutiveSummaryDTO summary = summaryService.generateNodeExecutiveSummary("IT_DIVISION", rawComments, "OPENAI");

        assertNotNull(summary);
        assertEquals("IT_DIVISION", summary.getNodeScope());
        assertEquals(3, summary.getTopStrengths().size(), "Must return exactly 3 top strengths per FR-AI-006");
        assertEquals(3, summary.getTopConcerns().size(), "Must return exactly 3 top concerns per FR-AI-006");
        assertEquals(2, summary.getRecommendations().size(), "Must return exactly 2 recommendations per FR-AI-006");
        assertNotNull(summary.getGeneratedAt());
    }
}
