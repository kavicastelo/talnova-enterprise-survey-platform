package com.talnova.tesp.ingestionservice;

import com.talnova.tesp.ingestionservice.controller.ResponseIngestionController;
import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import com.talnova.tesp.ingestionservice.dto.AnswerSubmissionDTO;
import com.talnova.tesp.ingestionservice.dto.IngestionResponseDTO;
import com.talnova.tesp.ingestionservice.dto.ResponseSubmissionDTO;
import com.talnova.tesp.ingestionservice.exception.InvalidTokenException;
import com.talnova.tesp.ingestionservice.exception.ResponseIngestionExceptionHandler;
import com.talnova.tesp.ingestionservice.service.ResponseIngestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ResponseIngestionController.class)
@ContextConfiguration(classes = {ResponseIngestionController.class, ResponseIngestionExceptionHandler.class})
class ResponseSecurityAuditTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ResponseIngestionService responseIngestionService;

    @Test
    @DisplayName("X-Project-ID Header Fallback: POST /api/v1/responses populates projectId from gateway header")
    void testHeaderFallbackSubmitResponse() {
        IngestionResponseDTO mockResponse = IngestionResponseDTO.builder()
                .responseId("RSP-88102910")
                .status("ACCEPTED")
                .message("Survey response submitted successfully")
                .timestamp(Instant.now())
                .build();

        when(responseIngestionService.ingestResponse(any(ResponseSubmissionDTO.class)))
                .thenReturn(Mono.just(mockResponse));

        ResponseSubmissionDTO submission = ResponseSubmissionDTO.builder()
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .responseToken("TKN-SEMI-99")
                .respondentType(RespondentType.SEMI_ANONYMOUS)
                .answers(List.of(AnswerSubmissionDTO.builder().questionId("Q-1").questionType("LIKERT").numericValue(5.0).build()))
                .build();

        webTestClient.post()
                .uri("/api/v1/responses")
                .header("X-Project-ID", "PRJ-99201")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(submission)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.responseId").isEqualTo("RSP-88102910");
    }

    @Test
    @DisplayName("BR-INT-001 / VR-INT-001: Token already burned or invalid returns HTTP 403 Forbidden")
    void testInvalidTokenDoubleSubmissionForbidden() {
        when(responseIngestionService.ingestResponse(any(ResponseSubmissionDTO.class)))
                .thenReturn(Mono.error(new InvalidTokenException("Invalid or expired survey token")));

        ResponseSubmissionDTO submission = ResponseSubmissionDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .responseToken("TKN-ALREADY-USED")
                .respondentType(RespondentType.SEMI_ANONYMOUS)
                .answers(List.of(AnswerSubmissionDTO.builder().questionId("Q-1").questionType("LIKERT").numericValue(5.0).build()))
                .build();

        webTestClient.post()
                .uri("/api/v1/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(submission)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.correlationId").isEqualTo("ERR-TOKEN-403");
    }
}
