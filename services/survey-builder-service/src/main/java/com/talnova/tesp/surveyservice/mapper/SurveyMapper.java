package com.talnova.tesp.surveyservice.mapper;

import com.talnova.tesp.surveyservice.domain.model.LogicRule;
import com.talnova.tesp.surveyservice.domain.model.SurveyDocument;
import com.talnova.tesp.surveyservice.domain.model.SurveyPage;
import com.talnova.tesp.surveyservice.domain.model.SurveyQuestion;
import com.talnova.tesp.surveyservice.domain.model.SurveySection;
import com.talnova.tesp.surveyservice.dto.LogicRuleDTO;
import com.talnova.tesp.surveyservice.dto.SurveyPageDTO;
import com.talnova.tesp.surveyservice.dto.SurveyQuestionDTO;
import com.talnova.tesp.surveyservice.dto.SurveyResponseDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.dto.SurveySectionDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for mapping between Survey Document entities and DTOs.
 */
@Component
public class SurveyMapper {

    public SurveyDocument toDocument(SurveySaveDraftDTO dto) {
        if (dto == null) {
            return null;
        }

        List<SurveyPage> pages = dto.getPages() != null
                ? dto.getPages().stream().map(this::toPageEntity).collect(Collectors.toList())
                : new ArrayList<>();

        return SurveyDocument.builder()
                .projectId(dto.getProjectId())
                .surveyId(dto.getSurveyId())
                .title(dto.getTitle() != null ? new HashMap<>(dto.getTitle()) : new HashMap<>())
                .description(dto.getDescription() != null ? new HashMap<>(dto.getDescription()) : new HashMap<>())
                .pages(pages)
                .build();
    }

    public SurveyResponseDTO toResponseDTO(SurveyDocument doc) {
        if (doc == null) {
            return null;
        }

        List<SurveyPageDTO> pages = doc.getPages() != null
                ? doc.getPages().stream().map(this::toPageDTO).collect(Collectors.toList())
                : new ArrayList<>();

        return SurveyResponseDTO.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .surveyId(doc.getSurveyId())
                .version(doc.getVersion())
                .title(doc.getTitle() != null ? new HashMap<>(doc.getTitle()) : new HashMap<>())
                .description(doc.getDescription() != null ? new HashMap<>(doc.getDescription()) : new HashMap<>())
                .status(doc.getStatus())
                .pages(pages)
                .publishedAt(doc.getPublishedAt())
                .versionHistory(doc.getVersionHistory() != null ? new ArrayList<>(doc.getVersionHistory()) : new ArrayList<>())
                .isDeleted(doc.isDeleted())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    public SurveyPage toPageEntity(SurveyPageDTO dto) {
        if (dto == null) return null;
        List<SurveySection> sections = dto.getSections() != null
                ? dto.getSections().stream().map(this::toSectionEntity).collect(Collectors.toList())
                : new ArrayList<>();
        return SurveyPage.builder()
                .pageId(dto.getPageId())
                .pageOrder(dto.getPageOrder())
                .title(dto.getTitle() != null ? new HashMap<>(dto.getTitle()) : new HashMap<>())
                .sections(sections)
                .build();
    }

    public SurveyPageDTO toPageDTO(SurveyPage entity) {
        if (entity == null) return null;
        List<SurveySectionDTO> sections = entity.getSections() != null
                ? entity.getSections().stream().map(this::toSectionDTO).collect(Collectors.toList())
                : new ArrayList<>();
        return SurveyPageDTO.builder()
                .pageId(entity.getPageId())
                .pageOrder(entity.getPageOrder())
                .title(entity.getTitle() != null ? new HashMap<>(entity.getTitle()) : new HashMap<>())
                .sections(sections)
                .build();
    }

    public SurveySection toSectionEntity(SurveySectionDTO dto) {
        if (dto == null) return null;
        List<SurveyQuestion> questions = dto.getQuestions() != null
                ? dto.getQuestions().stream().map(this::toQuestionEntity).collect(Collectors.toList())
                : new ArrayList<>();
        return SurveySection.builder()
                .sectionId(dto.getSectionId())
                .title(dto.getTitle() != null ? new HashMap<>(dto.getTitle()) : new HashMap<>())
                .questions(questions)
                .build();
    }

    public SurveySectionDTO toSectionDTO(SurveySection entity) {
        if (entity == null) return null;
        List<SurveyQuestionDTO> questions = entity.getQuestions() != null
                ? entity.getQuestions().stream().map(this::toQuestionDTO).collect(Collectors.toList())
                : new ArrayList<>();
        return SurveySectionDTO.builder()
                .sectionId(entity.getSectionId())
                .title(entity.getTitle() != null ? new HashMap<>(entity.getTitle()) : new HashMap<>())
                .questions(questions)
                .build();
    }

    public SurveyQuestion toQuestionEntity(SurveyQuestionDTO dto) {
        if (dto == null) return null;
        List<LogicRule> rules = dto.getLogicRules() != null
                ? dto.getLogicRules().stream().map(this::toRuleEntity).collect(Collectors.toList())
                : new ArrayList<>();
        return SurveyQuestion.builder()
                .questionId(dto.getQuestionId())
                .type(dto.getType())
                .groupId(dto.getGroupId())
                .prompt(dto.getPrompt() != null ? new HashMap<>(dto.getPrompt()) : new HashMap<>())
                .isMandatory(dto.isMandatory())
                .logicRules(rules)
                .build();
    }

    public SurveyQuestionDTO toQuestionDTO(SurveyQuestion entity) {
        if (entity == null) return null;
        List<LogicRuleDTO> rules = entity.getLogicRules() != null
                ? entity.getLogicRules().stream().map(this::toRuleDTO).collect(Collectors.toList())
                : new ArrayList<>();
        return SurveyQuestionDTO.builder()
                .questionId(entity.getQuestionId())
                .type(entity.getType())
                .groupId(entity.getGroupId())
                .prompt(entity.getPrompt() != null ? new HashMap<>(entity.getPrompt()) : new HashMap<>())
                .isMandatory(entity.isMandatory())
                .logicRules(rules)
                .build();
    }

    public LogicRule toRuleEntity(LogicRuleDTO dto) {
        if (dto == null) return null;
        return LogicRule.builder()
                .ruleId(dto.getRuleId())
                .operator(dto.getOperator())
                .comparisonValue(dto.getComparisonValue())
                .targetPageId(dto.getTargetPageId())
                .build();
    }

    public LogicRuleDTO toRuleDTO(LogicRule entity) {
        if (entity == null) return null;
        return LogicRuleDTO.builder()
                .ruleId(entity.getRuleId())
                .operator(entity.getOperator())
                .comparisonValue(entity.getComparisonValue())
                .targetPageId(entity.getTargetPageId())
                .build();
    }
}
