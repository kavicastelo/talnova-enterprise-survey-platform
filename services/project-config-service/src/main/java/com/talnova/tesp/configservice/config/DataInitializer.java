package com.talnova.tesp.configservice.config;

import com.talnova.tesp.configservice.domain.BrandingConfig;
import com.talnova.tesp.configservice.domain.FeatureFlags;
import com.talnova.tesp.configservice.domain.ProjectDocument;
import com.talnova.tesp.configservice.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ProjectRepository projectRepository;

    public DataInitializer(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void run(String... args) {
        String defaultProjectId = "PRJ-DEFAULT-001";
        try {
            if (!projectRepository.existsByProjectIdAndIsDeletedFalse(defaultProjectId)) {
                log.info("Seeding default project workspace '{}'...", defaultProjectId);

                BrandingConfig branding = BrandingConfig.builder()
                        .companyName("Talnova Enterprise")
                        .logoUrl("https://example.com/logo.png")
                        .primaryColor("#0f172a")
                        .secondaryColor("#3b82f6")
                        .customCssUrl("")
                        .build();

                FeatureFlags features = FeatureFlags.builder()
                        .aiAnalyticsEnabled(true)
                        .actionPlanningEnabled(true)
                        .kioskModeEnabled(false)
                        .smsDistributionEnabled(false)
                        .build();

                ProjectDocument defaultProject = ProjectDocument.builder()
                        .projectId(defaultProjectId)
                        .name("Default Survey Project")
                        .status(com.talnova.tesp.configservice.domain.ProjectStatus.ACTIVE)
                        .branding(branding)
                        .supportedLocales(List.of("en-US"))
                        .defaultLocale("en-US")
                        .features(features)
                        .customAttributeDefinitions(Collections.emptyList())
                        .version(0)
                        .isDeleted(false)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                projectRepository.save(defaultProject);
                log.info("Default project workspace '{}' successfully seeded into MongoDB.", defaultProjectId);
            }
        } catch (Exception ex) {
            log.debug("Notice: DataInitializer skipped: {}", ex.getMessage());
        }
    }
}
