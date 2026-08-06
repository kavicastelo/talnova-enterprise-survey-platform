package com.talnova.tesp.surveyservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.surveyservice.domain.model.OutboxEventDocument;
import com.talnova.tesp.surveyservice.domain.model.OutboxStatus;
import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import com.talnova.tesp.surveyservice.domain.model.SurveyDocument;
import com.talnova.tesp.surveyservice.domain.model.SurveyPage;
import com.talnova.tesp.surveyservice.domain.model.SurveyQuestion;
import com.talnova.tesp.surveyservice.domain.model.SurveySection;
import com.talnova.tesp.surveyservice.domain.model.SurveyStatus;
import com.talnova.tesp.surveyservice.dto.SurveyPageDTO;
import com.talnova.tesp.surveyservice.dto.SurveyQuestionDTO;
import com.talnova.tesp.surveyservice.dto.SurveyResponseDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.dto.SurveySectionDTO;
import com.talnova.tesp.surveyservice.dto.event.SurveyPublishedEvent;
import com.talnova.tesp.surveyservice.exception.SurveyNotFoundException;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import com.talnova.tesp.surveyservice.mapper.SurveyMapper;
import com.talnova.tesp.surveyservice.repository.OutboxEventRepository;
import com.talnova.tesp.surveyservice.repository.SurveyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of SurveyBuilderService handling survey draft persistence, version locking, logic compilation, and business rule validations.
 */
@Service
public class SurveyBuilderServiceImpl implements SurveyBuilderService {

    private static final Logger log = LoggerFactory.getLogger(SurveyBuilderServiceImpl.class);

    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final LogicASTCompiler logicASTCompiler;
    private final LocaleDictionaryResolver localeDictionaryResolver;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public SurveyBuilderServiceImpl(SurveyRepository surveyRepository,
                                  SurveyMapper surveyMapper,
                                  LogicASTCompiler logicASTCompiler,
                                  LocaleDictionaryResolver localeDictionaryResolver,
                                  OutboxEventRepository outboxEventRepository,
                                  ObjectMapper objectMapper) {
        this.surveyRepository = surveyRepository;
        this.surveyMapper = surveyMapper;
        this.logicASTCompiler = logicASTCompiler;
        this.localeDictionaryResolver = localeDictionaryResolver;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public SurveyResponseDTO saveDraft(String surveyId, SurveySaveDraftDTO dto) {
        log.info("Saving survey draft for surveyId: {}, projectId: {}", surveyId, dto.getProjectId());

        if (!surveyId.equals(dto.getSurveyId())) {
            throw new SurveyValidationException("Path surveyId '" + surveyId + "' does not match body surveyId '" + dto.getSurveyId() + "'");
        }

        validateQuestionGroups(dto);
        localeDictionaryResolver.validateSurveyLocales(dto, "en-US");
        logicASTCompiler.validateLogicRules(dto);

        Optional<SurveyDocument> existingDraftOpt = surveyRepository.findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse(
                dto.getProjectId(), surveyId, SurveyStatus.DRAFT);

        SurveyDocument documentToSave;
        if (existingDraftOpt.isPresent()) {
            documentToSave = existingDraftOpt.get();
            SurveyDocument updatedFromDto = surveyMapper.toDocument(dto);
            documentToSave.setTitle(updatedFromDto.getTitle());
            documentToSave.setDescription(updatedFromDto.getDescription());
            documentToSave.setPages(updatedFromDto.getPages());
            documentToSave.setUpdatedAt(Instant.now());
        } else {
            Optional<SurveyDocument> latestDocOpt = surveyRepository
                    .findFirstByProjectIdAndSurveyIdAndIsDeletedFalseOrderByVersionDesc(dto.getProjectId(), surveyId);

            if (latestDocOpt.isPresent()) {
                SurveyDocument latestDoc = latestDocOpt.get();
                if (latestDoc.getStatus() == SurveyStatus.PUBLISHED || latestDoc.getStatus() == SurveyStatus.ACTIVE) {
                    throw new SurveyValidationException("Cannot modify structural design of a published survey version");
                }
            }

            documentToSave = surveyMapper.toDocument(dto);
            documentToSave.setVersion(1);
            documentToSave.setStatus(SurveyStatus.DRAFT);
            documentToSave.setDeleted(false);
            documentToSave.setCreatedAt(Instant.now());
            documentToSave.setUpdatedAt(Instant.now());
        }

        SurveyDocument savedDoc = surveyRepository.save(documentToSave);
        log.info("Survey draft saved successfully with ID: {}, version: {}", savedDoc.getId(), savedDoc.getVersion());
        return surveyMapper.toResponseDTO(savedDoc);
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyResponseDTO getSurvey(String projectId, String surveyId) {
        log.info("Retrieving latest survey AST for surveyId: {}, projectId: {}", surveyId, projectId);

        SurveyDocument document = surveyRepository
                .findFirstByProjectIdAndSurveyIdAndIsDeletedFalseOrderByVersionDesc(projectId, surveyId)
                .orElseThrow(() -> new SurveyNotFoundException("Survey not found for surveyId: " + surveyId + " in project: " + projectId));

        return surveyMapper.toResponseDTO(document);
    }

    @Override
    @Transactional
    public SurveyResponseDTO publishSurvey(String projectId, String surveyId) {
        log.info("Publishing survey version for surveyId: {}, projectId: {}", surveyId, projectId);

        SurveyDocument draftDoc = surveyRepository
                .findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse(projectId, surveyId, SurveyStatus.DRAFT)
                .orElseThrow(() -> new SurveyNotFoundException("No active draft found to publish for surveyId: " + surveyId + " in project: " + projectId));

        validateSurveyNotEmpty(draftDoc);

        draftDoc.setStatus(SurveyStatus.PUBLISHED);
        draftDoc.setPublishedAt(Instant.now());
        if (draftDoc.getVersionHistory() == null) {
            draftDoc.setVersionHistory(new ArrayList<>());
        }
        if (!draftDoc.getVersionHistory().contains(draftDoc.getVersion())) {
            draftDoc.getVersionHistory().add(draftDoc.getVersion());
        }
        draftDoc.setUpdatedAt(Instant.now());

        SurveyDocument publishedDoc = surveyRepository.save(draftDoc);
        log.info("Survey version {} published successfully for surveyId: {}", publishedDoc.getVersion(), surveyId);

        // Record SurveyPublishedEvent into Outbox collection for asynchronous Kafka dispatch
        recordOutboxPublishedEvent(publishedDoc);

        return surveyMapper.toResponseDTO(publishedDoc);
    }

    @Override
    @Transactional
    public SurveyResponseDTO createDraftVersion(String projectId, String surveyId) {
        log.info("Creating new draft version for surveyId: {}, projectId: {}", surveyId, projectId);

        Optional<SurveyDocument> activeDraftOpt = surveyRepository
                .findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse(projectId, surveyId, SurveyStatus.DRAFT);
        if (activeDraftOpt.isPresent()) {
            return surveyMapper.toResponseDTO(activeDraftOpt.get());
        }

        SurveyDocument latestDoc = surveyRepository
                .findFirstByProjectIdAndSurveyIdAndIsDeletedFalseOrderByVersionDesc(projectId, surveyId)
                .orElseThrow(() -> new SurveyNotFoundException("Survey not found for surveyId: " + surveyId + " in project: " + projectId));

        int nextVersion = (latestDoc.getVersion() != null ? latestDoc.getVersion() : 1) + 1;

        SurveyDocument newDraftDoc = SurveyDocument.builder()
                .projectId(projectId)
                .surveyId(surveyId)
                .version(nextVersion)
                .title(latestDoc.getTitle())
                .description(latestDoc.getDescription())
                .status(SurveyStatus.DRAFT)
                .pages(latestDoc.getPages())
                .versionHistory(latestDoc.getVersionHistory() != null ? new ArrayList<>(latestDoc.getVersionHistory()) : new ArrayList<>())
                .isDeleted(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        SurveyDocument savedDraft = surveyRepository.save(newDraftDoc);
        log.info("New draft version {} created for surveyId: {}", savedDraft.getVersion(), surveyId);
        return surveyMapper.toResponseDTO(savedDraft);
    }

    private void recordOutboxPublishedEvent(SurveyDocument publishedDoc) {
        int questionCount = countTotalQuestions(publishedDoc);
        String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        SurveyPublishedEvent publishedEvent = SurveyPublishedEvent.builder()
                .eventId(eventId)
                .eventType("SURVEY_PUBLISHED")
                .projectId(publishedDoc.getProjectId())
                .surveyId(publishedDoc.getSurveyId())
                .version(publishedDoc.getVersion())
                .questionCount(questionCount)
                .timestamp(Instant.now())
                .build();

        try {
            String payloadJson = objectMapper.writeValueAsString(publishedEvent);
            OutboxEventDocument outboxEvent = OutboxEventDocument.builder()
                    .eventId(eventId)
                    .eventType("SURVEY_PUBLISHED")
                    .projectId(publishedDoc.getProjectId())
                    .payload(payloadJson)
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.info("Outbox event {} saved successfully for published survey {}", eventId, publishedDoc.getSurveyId());
        } catch (Exception ex) {
            log.error("Failed to serialize SurveyPublishedEvent outbox message: {}", ex.getMessage(), ex);
        }
    }

    private int countTotalQuestions(SurveyDocument doc) {
        if (doc.getPages() == null) return 0;
        int count = 0;
        for (SurveyPage page : doc.getPages()) {
            if (page.getSections() == null) continue;
            for (SurveySection section : page.getSections()) {
                if (section.getQuestions() != null) {
                    count += section.getQuestions().size();
                }
            }
        }
        return count;
    }

    private void validateQuestionGroups(SurveySaveDraftDTO dto) {
        if (dto.getPages() == null) return;

        for (SurveyPageDTO page : dto.getPages()) {
            if (page.getSections() == null) continue;
            for (SurveySectionDTO section : page.getSections()) {
                if (section.getQuestions() == null) continue;
                for (SurveyQuestionDTO question : section.getQuestions()) {
                    QuestionType type = question.getType();
                    if (type == QuestionType.LIKERT || type == QuestionType.NPS || type == QuestionType.MATRIX) {
                        if (question.getGroupId() == null || question.getGroupId().isBlank()) {
                            throw new SurveyValidationException(
                                    "Mandatory Question Group missing for quantitative question type: " + type + " (questionId: " + question.getQuestionId() + ")"
                            );
                        }
                    }
                }
            }
        }
    }

    private void validateSurveyNotEmpty(SurveyDocument doc) {
        if (doc.getPages() == null || doc.getPages().isEmpty()) {
            throw new SurveyValidationException("Cannot publish empty survey");
        }
        boolean hasSection = false;
        boolean hasQuestion = false;
        for (SurveyPage page : doc.getPages()) {
            if (page.getSections() != null && !page.getSections().isEmpty()) {
                hasSection = true;
                for (SurveySection section : page.getSections()) {
                    if (section.getQuestions() != null && !section.getQuestions().isEmpty()) {
                        hasQuestion = true;
                        break;
                    }
                }
            }
        }
        if (!hasSection || !hasQuestion) {
            throw new SurveyValidationException("Cannot publish empty survey");
        }
    }
}
