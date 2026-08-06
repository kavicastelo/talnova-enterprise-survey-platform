package com.talnova.tesp.ingestionservice;

import com.talnova.tesp.ingestionservice.dto.AnswerSubmissionDTO;
import com.talnova.tesp.ingestionservice.dto.ResponseSubmissionDTO;
import com.talnova.tesp.ingestionservice.exception.InvalidAnswerException;
import com.talnova.tesp.ingestionservice.service.AnswerAstValidatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class AnswerAstValidatorServiceTest {

    private AnswerAstValidatorServiceImpl validatorService;

    @BeforeEach
    void setUp() {
        validatorService = new AnswerAstValidatorServiceImpl();
    }

    @Test
    @DisplayName("TC-INT-301-01: Valid LIKERT 1-5 and NPS 0-10 answers pass validation successfully")
    void testValidateAnswersSuccess() {
        ResponseSubmissionDTO submission = ResponseSubmissionDTO.builder()
                .answers(List.of(
                        AnswerSubmissionDTO.builder().questionId("Q-101").questionType("LIKERT").numericValue(5.0).build(),
                        AnswerSubmissionDTO.builder().questionId("Q-102").questionType("NPS").numericValue(10.0).build()
                ))
                .build();

        Mono<Void> result = validatorService.validateAnswers(submission);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("TC-INT-301-02: Out-of-range LIKERT score 7 throws InvalidAnswerException")
    void testLikertScoreOutOfRange() {
        ResponseSubmissionDTO submission = ResponseSubmissionDTO.builder()
                .answers(List.of(
                        AnswerSubmissionDTO.builder().questionId("Q-101").questionType("LIKERT").numericValue(7.0).build()
                ))
                .build();

        Mono<Void> result = validatorService.validateAnswers(submission);

        StepVerifier.create(result)
                .expectErrorMatches(t -> t instanceof InvalidAnswerException &&
                        t.getMessage().contains("Numeric score out of range (1-5) for LIKERT questionId: Q-101"))
                .verify();
    }

    @Test
    @DisplayName("TC-INT-301-03: Out-of-range NPS score 11 throws InvalidAnswerException")
    void testNpsScoreOutOfRange() {
        ResponseSubmissionDTO submission = ResponseSubmissionDTO.builder()
                .answers(List.of(
                        AnswerSubmissionDTO.builder().questionId("Q-102").questionType("NPS").numericValue(11.0).build()
                ))
                .build();

        Mono<Void> result = validatorService.validateAnswers(submission);

        StepVerifier.create(result)
                .expectErrorMatches(t -> t instanceof InvalidAnswerException &&
                        t.getMessage().contains("Numeric score out of range (0-10) for NPS questionId: Q-102"))
                .verify();
    }
}
