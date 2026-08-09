package com.talnova.tesp.aiservice.config;

import com.talnova.tesp.aiservice.domain.model.AiInsightDocument;
import com.talnova.tesp.aiservice.domain.model.RiskSeverity;
import com.talnova.tesp.aiservice.domain.model.SentimentLabel;
import com.talnova.tesp.aiservice.repository.AiInsightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final AiInsightRepository aiInsightRepository;

    public DataInitializer(AiInsightRepository aiInsightRepository) {
        this.aiInsightRepository = aiInsightRepository;
    }

    @Override
    public void run(String... args) {
        seedInsights("PRJ-99201");
        seedInsights("PRJ-DEFAULT-001");
    }

    private void seedInsights(String projectId) {
        try {
            if (!aiInsightRepository.existsByProjectId(projectId)) {
                log.info("Seeding demo AI insights for project '{}'...", projectId);

                AiInsightDocument insight1 = AiInsightDocument.builder()
                        .projectId(projectId)
                        .campaignId("CMP-77102")
                        .responseId("RSP-1001")
                        .questionId("Q-001")
                        .sanitizedText("Great workplace culture and flexible hours, but cross-team communication could improve.")
                        .sentimentScore(0.78)
                        .sentimentLabel(SentimentLabel.POSITIVE)
                        .confidence(0.92)
                        .themes(List.of("Culture", "Flexibility", "Communication"))
                        .riskCategory("LOW_ATTRITION_RISK")
                        .riskSeverity(RiskSeverity.LOW)
                        .createdAt(Instant.now())
                        .build();

                AiInsightDocument insight2 = AiInsightDocument.builder()
                        .projectId(projectId)
                        .campaignId("CMP-77102")
                        .responseId("RSP-1002")
                        .questionId("Q-002")
                        .sanitizedText("Heavy workload in Q2 caused burnout across senior engineering roles.")
                        .sentimentScore(-0.65)
                        .sentimentLabel(SentimentLabel.NEGATIVE)
                        .confidence(0.89)
                        .themes(List.of("Burnout", "Workload", "Engineering"))
                        .riskCategory("BURNOUT_WARNING")
                        .riskSeverity(RiskSeverity.HIGH)
                        .createdAt(Instant.now())
                        .build();

                aiInsightRepository.save(insight1);
                aiInsightRepository.save(insight2);
                log.info("Seeded AI insights for project '{}'", projectId);
            }
        } catch (Exception ex) {
            log.debug("Notice: AI DataInitializer skipped for {}: {}", projectId, ex.getMessage());
        }
    }
}
