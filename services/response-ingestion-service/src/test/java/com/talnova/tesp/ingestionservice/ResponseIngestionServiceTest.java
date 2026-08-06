package com.talnova.tesp.ingestionservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import com.talnova.tesp.ingestionservice.domain.model.SurveyResponseDocument;
import com.talnova.tesp.ingestionservice.dto.AnswerSubmissionDTO;
import com.talnova.tesp.ingestionservice.dto.IngestionResponseDTO;
import com.talnova.tesp.ingestionservice.dto.ResponseSubmissionDTO;
import com.talnova.tesp.ingestionservice.event.ReactiveKafkaResponseProducer;
import com.talnova.tesp.ingestionservice.exception.InvalidTokenException;
import com.talnova.tesp.ingestionservice.repository.ResponseRepository;
import com.talnova.tesp.ingestionservice.service.AnswerAstValidatorServiceImpl;
import com.talnova.tesp.ingestionservice.service.AtomicTokenBurnerService;
import com.talnova.tesp.ingestionservice.service.DemographicSnapshotEmbedderServiceImpl;
import com.talnova.tesp.ingestionservice.service.PiiScrubberServiceImpl;
import com.talnova.tesp.ingestionservice.service.ResponseIngestionServiceImpl;
import com.talnova.tesp.ingestionservice.service.XssSanitizerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponseIngestionServiceTest {

    @Mock
    private ResponseRepository responseRepository;

    @Mock
    private AtomicTokenBurnerService tokenBurnerService;

    @Mock
    private ReactiveKafkaResponseProducer kafkaResponseProducer;

    private ResponseIngestionServiceImpl responseIngestionService;

    @BeforeEach
    void setUp() {
        responseIngestionService = new ResponseIngestionServiceImpl(
                responseRepository,
                tokenBurnerService,
                new AnswerAstValidatorServiceImpl(),
                new DemographicSnapshotEmbedderServiceImpl(new ObjectMapper()),
                kafkaResponseProducer,
                new PiiScrubberServiceImpl(),
                new XssSanitizerServiceImpl()
        );
    }

    @Test
    @DisplayName("TC-INT-801-02: Ingest valid response submission with XSS & PII sanitization and token burn")
    void testIngestResponseSuccess() {
        ResponseSubmissionDTO submission = ResponseSubmissionDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .respondentType(RespondentType.SEMI_ANONYMOUS)
                .responseToken("TKN-SEMI-101")
                .nodeId("N-301")
                .answers(List.of(
                        AnswerSubmissionDTO.builder().questionId("Q-101").questionType("LIKERT").numericValue(5.0).build(),
                        AnswerSubmissionDTO.builder().questionId("Q-102").questionType("LONG_TEXT").textValue("<script>alert('xss')</script>Contact john@aitkenspence.lk").build()
                ))
                .build();

        when(tokenBurnerService.burnToken("TKN-SEMI-101")).thenReturn(Mono.just("{\"Tenure\":\"3-5 Years\"}"));
        when(responseRepository.save(any(SurveyResponseDocument.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(kafkaResponseProducer.publishResponseEvent(any())).thenReturn(Mono.empty());

        Mono<IngestionResponseDTO> result = responseIngestionService.ingestResponse(submission);

        StepVerifier.create(result)
                .assertNext(res -> {
                    assertNotNull(res.getResponseId());
                    assertTrue(res.getResponseId().startsWith("RSP-"));
                    assertEquals("ACCEPTED", res.getStatus());
                })
                .verifyComplete();

        verify(tokenBurnerService, times(1)).burnToken("TKN-SEMI-101");
        verify(responseRepository, times(1)).save(any(SurveyResponseDocument.class));
        verify(kafkaResponseProducer, times(1)).publishResponseEvent(any());
    }

    @Test
    @DisplayName("TC-INT-801-03: Double submission attempt with already burned token throws InvalidTokenException")
    void testDoubleSubmissionRejected() {
        ResponseSubmissionDTO submission = ResponseSubmissionDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .respondentType(RespondentType.SEMI_ANONYMOUS)
                .responseToken("TKN-USED-999")
                .answers(List.of(AnswerSubmissionDTO.builder().questionId("Q-101").questionType("LIKERT").numericValue(4.0).build()))
                .build();

        when(tokenBurnerService.burnToken("TKN-USED-999")).thenReturn(Mono.empty());

        Mono<IngestionResponseDTO> result = responseIngestionService.ingestResponse(submission);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidTokenException &&
                        throwable.getMessage().contains("Invalid or expired survey token"))
                .verify();

        verify(responseRepository, never()).save(any());
        verify(kafkaResponseProducer, never()).publishResponseEvent(any());
    }
}
