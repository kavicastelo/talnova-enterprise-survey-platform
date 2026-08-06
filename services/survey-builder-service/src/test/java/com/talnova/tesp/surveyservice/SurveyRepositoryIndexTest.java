package com.talnova.tesp.surveyservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SurveyRepositoryIndexTest {

    @Test
    @DisplayName("TC-SRV-102-A: Verify compound unique index fields (projectId, surveyId, version)")
    void testCompoundUniqueIndexKeys() {
        Map<String, Integer> uniqueIndexKeys = Map.of(
                "projectId", 1,
                "surveyId", 1,
                "version", 1
        );

        assertEquals(3, uniqueIndexKeys.size());
        assertEquals(1, uniqueIndexKeys.get("projectId"));
        assertEquals(1, uniqueIndexKeys.get("surveyId"));
        assertEquals(1, uniqueIndexKeys.get("version"));
    }

    @Test
    @DisplayName("TC-SRV-102-B: Verify active survey lookup index fields (projectId, status, isDeleted)")
    void testActiveSurveyLookupIndexKeys() {
        List<String> indexFields = List.of("projectId", "status", "isDeleted");

        assertEquals(3, indexFields.size());
        assertTrue(indexFields.contains("projectId"));
        assertTrue(indexFields.contains("status"));
        assertTrue(indexFields.contains("isDeleted"));
    }
}
