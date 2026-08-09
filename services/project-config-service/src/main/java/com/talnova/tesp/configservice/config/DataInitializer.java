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
        seedProject("PRJ-DEFAULT-001", "Default Survey Project");
        seedProject("PRJ-99201", "Aitken Spence Enterprise Workspace");
    }

    private void seedProject(String projectId, String name) {
        try {
            if (!projectRepository.existsByProjectIdAndIsDeletedFalse(projectId)) {
                log.info("Seeding default project workspace '{}'...", projectId);

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
                        .kioskModeEnabled(true)
                        .smsDistributionEnabled(true)
                        .build();

                ProjectDocument defaultProject = ProjectDocument.builder()
                        .projectId(projectId)
                        .name(name)
                        .status(com.talnova.tesp.configservice.domain.ProjectStatus.ACTIVE)
                        .branding(branding)
                        .supportedLocales(List.of("en-US", "es-ES"))
                        .defaultLocale("en-US")
                        .features(features)
                        .customAttributeDefinitions(Collections.emptyList())
                        .isDeleted(false)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                projectRepository.save(defaultProject);
                log.info("Project workspace '{}' successfully seeded into MongoDB.", projectId);
            }
        } catch (Exception ex) {
            log.error("DataInitializer error for {}:", projectId, ex);
        }
    }
}
