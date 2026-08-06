package com.talnova.tesp.surveyservice.mapper;

import com.talnova.tesp.surveyservice.domain.model.QuestionLibraryDocument;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryCreateDTO;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Mapper for Question Library entities and DTOs.
 */
@Component
public class QuestionLibraryMapper {

    public QuestionLibraryDocument toDocument(QuestionLibraryCreateDTO dto) {
        if (dto == null) return null;
        return QuestionLibraryDocument.builder()
                .libraryId(dto.getLibraryId())
                .category(dto.getCategory())
                .type(dto.getType())
                .groupId(dto.getGroupId())
                .prompt(dto.getPrompt() != null ? new HashMap<>(dto.getPrompt()) : new HashMap<>())
                .tags(dto.getTags() != null ? new ArrayList<>(dto.getTags()) : new ArrayList<>())
                .build();
    }

    public QuestionLibraryResponseDTO toResponseDTO(QuestionLibraryDocument doc) {
        if (doc == null) return null;
        return QuestionLibraryResponseDTO.builder()
                .id(doc.getId())
                .libraryId(doc.getLibraryId())
                .category(doc.getCategory())
                .type(doc.getType())
                .groupId(doc.getGroupId())
                .prompt(doc.getPrompt() != null ? new HashMap<>(doc.getPrompt()) : new HashMap<>())
                .tags(doc.getTags() != null ? new ArrayList<>(doc.getTags()) : new ArrayList<>())
                .isDeleted(doc.isDeleted())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
