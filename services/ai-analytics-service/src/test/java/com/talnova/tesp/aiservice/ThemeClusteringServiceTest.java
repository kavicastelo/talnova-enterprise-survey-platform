package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.dto.ThemeClusterDTO;
import com.talnova.tesp.aiservice.sentiment.ThemeClusteringServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThemeClusteringServiceTest {

    private ThemeClusteringServiceImpl themeClusteringService;

    @BeforeEach
    void setUp() {
        themeClusteringService = new ThemeClusteringServiceImpl();
    }

    @Test
    @DisplayName("TC-AI-302-01: Extract themes from single comment")
    void testExtractThemesFromComment() {
        String comment = "We need a workload audit and better development workshops for team growth.";

        List<String> themes = themeClusteringService.extractThemesFromComment(comment);

        assertNotNull(themes);
        assertTrue(themes.contains("Workload Audit"));
        assertTrue(themes.contains("Development Workshops"));
    }

    @Test
    @DisplayName("TC-AI-302-02: Extract top themes across department comments per US-AI-003 & FR-AI-003")
    void testExtractTopThemes() {
        List<String> comments = List.of(
                "High workload and tight deadlines this quarter.",
                "We urgently need a workload audit in IT division.",
                "Great team collaboration and support.",
                "Development workshops were super helpful."
        );

        List<ThemeClusterDTO> topThemes = themeClusteringService.extractTopThemes(comments, 5);

        assertNotNull(topThemes);
        assertFalse(topThemes.isEmpty());
        assertEquals("Workload Audit", topThemes.get(0).getThemeName(), "Most frequent theme should be Workload Audit");
        assertEquals(2, topThemes.get(0).getFrequency());
    }
}
