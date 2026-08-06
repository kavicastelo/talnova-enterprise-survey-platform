package com.talnova.tesp.surveyservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryCreateDTO;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryResponseDTO;
import com.talnova.tesp.surveyservice.service.QuestionLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/question-library")
@Tag(name = "Question Library API", description = "Endpoints for saving and searching validated engagement question templates and catalogs")
public class QuestionLibraryController {

    private final QuestionLibraryService questionLibraryService;

    public QuestionLibraryController(QuestionLibraryService questionLibraryService) {
        this.questionLibraryService = questionLibraryService;
    }

    @PostMapping
    @Operation(summary = "Save Question Template to Catalog", description = "Saves a validated engagement question template into the centralized Question Library catalog.")
    public ResponseEntity<ApiResponse<QuestionLibraryResponseDTO>> createQuestionTemplate(
            @Valid @RequestBody QuestionLibraryCreateDTO dto,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        QuestionLibraryResponseDTO response = questionLibraryService.createQuestionTemplate(dto);
        URI location = URI.create("/api/v1/question-library/" + response.getLibraryId());
        return ResponseEntity.created(location)
                .body(ApiResponse.success(response, "Question template saved to Question Library", correlationId));
    }

    @GetMapping
    @Operation(summary = "Search Question Library Catalog", description = "Retrieves reusable question templates filtered by domain category, theme group, or keyword search.")
    public ResponseEntity<ApiResponse<List<QuestionLibraryResponseDTO>>> searchQuestions(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "search", required = false) String search,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        List<QuestionLibraryResponseDTO> responses = questionLibraryService.searchQuestions(category, search);
        return ResponseEntity.ok(ApiResponse.success(responses, "Question Library templates retrieved", correlationId));
    }
}
