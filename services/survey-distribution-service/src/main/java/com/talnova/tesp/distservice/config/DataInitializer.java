package com.talnova.tesp.distservice.config;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.CampaignMetrics;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final CampaignRepository campaignRepository;

    public DataInitializer(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public void run(String... args) {
        seedCampaign("PRJ-99201", "CMP-77102");
        seedCampaign("PRJ-DEFAULT-001", "CMP-77102");
    }

    private void seedCampaign(String projectId, String campaignId) {
        try {
            if (!campaignRepository.existsByProjectIdAndCampaignIdAndIsDeletedFalse(projectId, campaignId)) {
                log.info("Seeding demo survey campaign '{}' for project '{}'...", campaignId, projectId);

                CampaignMetrics metrics = CampaignMetrics.builder()
                        .totalTargeted(1500)
                        .sent(1480)
                        .delivered(1468)
                        .opened(1100)
                        .started(980)
                        .completed(920)
                        .bounced(12)
                        .build();

                SurveyCampaignDocument campaign = new SurveyCampaignDocument(
                        null,
                        projectId,
                        campaignId,
                        "SUR-88102",
                        1,
                        "Q3 Employee Engagement Campaign",
                        AnonymityLevel.FULLY_ANONYMOUS,
                        Collections.emptyList(),
                        null,
                        null,
                        metrics,
                        CampaignStatus.ACTIVE,
                        Instant.now().minus(7, ChronoUnit.DAYS),
                        Instant.now().plus(30, ChronoUnit.DAYS),
                        false,
                        null,
                        Instant.now(),
                        Instant.now()
                );

                campaignRepository.save(campaign);
                log.info("Successfully seeded campaign '{}' for project '{}'", campaignId, projectId);
            }
        } catch (Exception ex) {
            log.debug("Notice: Campaign DataInitializer skipped for {}: {}", projectId, ex.getMessage());
        }
    }
}
