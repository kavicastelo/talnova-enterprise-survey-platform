package com.talnova.tesp.aiservice.sentiment;

import com.talnova.tesp.aiservice.domain.model.SentimentLabel;
import com.talnova.tesp.aiservice.dto.ThemeClusterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThemeClusteringServiceImpl implements ThemeClusteringService {

    private static final Logger log = LoggerFactory.getLogger(ThemeClusteringServiceImpl.class);

    private static final Map<String, List<String>> THEME_KEYWORD_MAP = Map.of(
            "Workload Audit", List.of("workload", "deadlines", "hours", "overtime", "burnout", "busy"),
            "Development Workshops", List.of("training", "workshop", "development", "learning", "growth", "skills"),
            "Management Communication", List.of("communication", "management", "leadership", "feedback", "transparency"),
            "Workplace Safety", List.of("safety", "hazard", "equipment", "protective", "accident"),
            "Team Collaboration", List.of("collaboration", "team", "colleagues", "support", "cooperation")
    );

    @Override
    public List<String> extractThemesFromComment(String textComment) {
        if (textComment == null || textComment.isBlank()) {
            return Collections.emptyList();
        }

        String lowerText = textComment.toLowerCase(Locale.ROOT);
        List<String> matchedThemes = new ArrayList<>();

        THEME_KEYWORD_MAP.forEach((theme, keywords) -> {
            for (String kw : keywords) {
                if (lowerText.contains(kw)) {
                    matchedThemes.add(theme);
                    break;
                }
            }
        });

        if (matchedThemes.isEmpty()) {
            matchedThemes.add("General Feedback");
        }

        return matchedThemes;
    }

    @Override
    public List<ThemeClusterDTO> extractTopThemes(List<String> textComments, int topN) {
        if (textComments == null || textComments.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Integer> frequencyMap = new HashMap<>();

        for (String comment : textComments) {
            List<String> themes = extractThemesFromComment(comment);
            for (String theme : themes) {
                frequencyMap.put(theme, frequencyMap.getOrDefault(theme, 0) + 1);
            }
        }

        log.debug("Extracted theme clusters across {} comments", textComments.size());

        return frequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN > 0 ? topN : 10)
                .map(entry -> ThemeClusterDTO.builder()
                        .themeName(entry.getKey())
                        .frequency(entry.getValue())
                        .dominantSentiment(entry.getKey().contains("Safety") ? SentimentLabel.NEGATIVE : SentimentLabel.POSITIVE)
                        .build())
                .collect(Collectors.toList());
    }
}
