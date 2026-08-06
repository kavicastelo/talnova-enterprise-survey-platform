package com.talnova.tesp.ingestionservice.service;

import com.talnova.tesp.ingestionservice.dto.ResponseSubmissionDTO;
import reactor.core.publisher.Mono;

public interface AnswerAstValidatorService {

    /**
     * Validates submitted answer values against survey question type constraints (LIKERT 1-5, NPS 0-10, mandatory checks).
     *
     * @param submission Response submission request DTO
     * @return Mono.empty() if valid, or Mono.error(InvalidAnswerException) if validation fails.
     */
    Mono<Void> validateAnswers(ResponseSubmissionDTO submission);
}
