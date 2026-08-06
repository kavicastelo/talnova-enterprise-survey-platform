package com.talnova.tesp.employeeservice;

import com.talnova.tesp.employeeservice.dto.HeaderMapping;
import com.talnova.tesp.employeeservice.dto.HeaderMappingResponseDTO;
import com.talnova.tesp.employeeservice.service.ai.AiHeaderMappingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AiHeaderMappingTest {

    private AiHeaderMappingServiceImpl mappingService;

    @BeforeEach
    void setUp() {
        mappingService = new AiHeaderMappingServiceImpl();
    }

    @Test
    @DisplayName("TC-EMP-601-A: AI fuzzy header mapping maps unstandardized CSV headers to standard employee schema FR-EMP-008")
    void testFuzzyHeaderMappingAccuracy() {
        List<String> rawHeaders = List.of(
                "Emp ID",
                "Work Email",
                "Staff Name",
                "Org Node",
                "Tenure (Years)",
                "Sex"
        );

        HeaderMappingResponseDTO response = mappingService.mapHeaders(rawHeaders);

        assertNotNull(response);
        assertEquals(6, response.getMappings().size());

        HeaderMapping empIdMapping = response.getMappings().get(0);
        assertEquals("Emp ID", empIdMapping.getSourceHeader());
        assertEquals("employeeId", empIdMapping.getTargetAttributeKey());
        assertTrue(empIdMapping.getConfidence() >= 0.90);
        assertTrue(empIdMapping.isCoreField());

        HeaderMapping emailMapping = response.getMappings().get(1);
        assertEquals("Work Email", emailMapping.getSourceHeader());
        assertEquals("email", emailMapping.getTargetAttributeKey());
        assertTrue(emailMapping.getConfidence() >= 0.90);

        HeaderMapping tenureMapping = response.getMappings().get(4);
        assertEquals("Tenure (Years)", tenureMapping.getSourceHeader());
        assertEquals("TenureYears", tenureMapping.getTargetAttributeKey());
        assertFalse(tenureMapping.isCoreField());
    }
}
