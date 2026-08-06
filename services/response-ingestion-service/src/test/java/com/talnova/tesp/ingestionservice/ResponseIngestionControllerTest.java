package com.talnova.tesp.ingestionservice;

import com.talnova.tesp.ingestionservice.controller.ResponseIngestionController;
import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import com.talnova.tesp.ingestionservice.dto.AnswerSubmissionDTO;
import com.talnova.tesp.ingestionservice.dto.IngestionResponseDTO;
import com.talnova.tesp.ingestionservice.dto.ResponseSubmissionDTO;
import com.talnova.tesp.ingestionservice.service.ResponseIngestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponseIngestionControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private ResponseIngestionService responseIngestionService;

    @InjectMocks
    private ResponseIngestionController controller;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    @DisplayName("TC-INT-102-02: POST /api/v1/responses returns HTTP 202 Accepted")
    void testSubmitResponseEndpoint() {
        ResponseSubmissionDTO submission = ResponseSubmissionDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .respondentType(RespondentType.SEMI_ANONYMOUS)
                .responseToken("TKN-SEMI-101")
                .answers(List.of(
                        AnswerSubmissionDTO.builder().questionId("Q-101").questionType("LIKERT").numericValue(5.0).build()
                ))
                .build();

        IngestionResponseDTO responseDTO = IngestionResponseDTO.builder()
                .responseId("RSP-88201920")
                .status("ACCEPTED")
                .message("Survey response submitted successfully")
                .build();

        when(responseIngestionService.ingestResponse(any())).thenReturn(Mono.just(responseDTO));

        webTestClient.post()
                .uri("/api/v1/responses")
                .bodyValue(submission)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.responseId").isEqualTo("RSP-88201920")
                .jsonPath("$.data.status").isEqualTo("ACCEPTED");
    }
}
