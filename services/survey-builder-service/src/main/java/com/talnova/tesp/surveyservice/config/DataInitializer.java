package com.talnova.tesp.surveyservice.config;

import com.talnova.tesp.surveyservice.domain.model.SurveyDocument;
import com.talnova.tesp.surveyservice.domain.model.SurveyStatus;
import com.talnova.tesp.surveyservice.repository.SurveyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final SurveyRepository surveyRepository;

    public DataInitializer(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @Override
    public void run(String... args) {
        seedSurvey("PRJ-99201", "SUR-88102");
        seedSurvey("PRJ-DEFAULT-001", "SUR-88102");
    }

    private void seedSurvey(String projectId, String surveyId) {
        try {
            if (!surveyRepository.existsByProjectIdAndSurveyIdAndIsDeletedFalse(projectId, surveyId)) {
                log.info("Seeding demo survey AST '{}' for project '{}'...", surveyId, projectId);

                SurveyDocument survey = new SurveyDocument();
                survey.setProjectId(projectId);
                survey.setSurveyId(surveyId);
                survey.setTitle(Map.of("en-US", "Annual Employee Engagement & Culture Survey 2026"));
                survey.setDescription(Map.of("en-US", "Comprehensive organizational feedback survey measuring leadership, work flexibility, and career growth."));
                survey.setStatus(SurveyStatus.PUBLISHED);
                survey.setPages(Collections.emptyList());
                survey.setPublishedAt(Instant.now());
                survey.setVersionHistory(List.of(1));
                survey.setDeleted(false);
                survey.setCreatedAt(Instant.now());
                survey.setUpdatedAt(Instant.now());

                surveyRepository.save(survey);
                log.info("Successfully seeded survey '{}' for project '{}'", surveyId, projectId);
            }
        } catch (Exception ex) {
            log.debug("Notice: Survey DataInitializer skipped for {}: {}", projectId, ex.getMessage());
        }
    }
}
