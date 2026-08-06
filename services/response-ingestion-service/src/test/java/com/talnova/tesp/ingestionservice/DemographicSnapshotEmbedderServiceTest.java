package com.talnova.tesp.ingestionservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import com.talnova.tesp.ingestionservice.service.DemographicSnapshotEmbedderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DemographicSnapshotEmbedderServiceTest {

    private DemographicSnapshotEmbedderServiceImpl embedderService;

    @BeforeEach
    void setUp() {
        embedderService = new DemographicSnapshotEmbedderServiceImpl(new ObjectMapper());
    }

    @Test
    @DisplayName("TC-INT-302-01: Embed demographic tags and strip employeeId/email PII keys for SEMI_ANONYMOUS mode")
    void testEmbedDemographicSnapshotSemiAnonymous() {
        String tokenMetadata = "{\"employeeId\":\"EMP-10020\",\"email\":\"user@aitkenspence.lk\",\"Tenure\":\"3-5 Years\",\"Department\":\"Operations\"}";

        Map<String, String> snapshot = embedderService.embedDemographicSnapshot(tokenMetadata, RespondentType.SEMI_ANONYMOUS);

        assertNotNull(snapshot);
        assertEquals("3-5 Years", snapshot.get("Tenure"));
        assertEquals("Operations", snapshot.get("Department"));
        assertFalse(snapshot.containsKey("employeeId"), "employeeId must be stripped per BR-INT-002");
        assertFalse(snapshot.containsKey("email"), "email must be stripped per BR-INT-002");
    }

    @Test
    @DisplayName("TC-INT-302-02: Retain employeeId for AUTHENTICATED mode")
    void testEmbedDemographicSnapshotAuthenticated() {
        String tokenMetadata = "{\"employeeId\":\"EMP-10020\",\"Tenure\":\"1-2 Years\"}";

        Map<String, String> snapshot = embedderService.embedDemographicSnapshot(tokenMetadata, RespondentType.AUTHENTICATED);

        assertNotNull(snapshot);
        assertEquals("EMP-10020", snapshot.get("employeeId"));
        assertEquals("1-2 Years", snapshot.get("Tenure"));
    }
}
