package com.talnova.tesp.aiservice.sentiment;

import com.talnova.tesp.aiservice.dto.ThemeClusterDTO;

import java.util.List;

public interface ThemeClusteringService {

    /**
     * Extracts theme keywords from an individual text comment.
     */
    List<String> extractThemesFromComment(String textComment);

    /**
     * Aggregates comments across a Question Group / Organization Node and extracts top N recurring themes with frequency counts per FR-AI-003.
     */
    List<ThemeClusterDTO> extractTopThemes(List<String> textComments, int topN);
}
