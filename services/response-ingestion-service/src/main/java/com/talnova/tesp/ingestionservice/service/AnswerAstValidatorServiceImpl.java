package com.talnova.tesp.ingestionservice.service;

import com.talnova.tesp.ingestionservice.dto.AnswerSubmissionDTO;
import com.talnova.tesp.ingestionservice.dto.ResponseSubmissionDTO;
import com.talnova.tesp.ingestionservice.exception.InvalidAnswerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AnswerAstValidatorServiceImpl implements AnswerAstValidatorService {

    private static final Logger log = LoggerFactory.getLogger(AnswerAstValidatorServiceImpl.class);

    @Override
    public Mono<Void> validateAnswers(ResponseSubmissionDTO submission) {
        List<AnswerSubmissionDTO> answers = submission.getAnswers();
        if (answers == null || answers.isEmpty()) {
            return Mono.error(new InvalidAnswerException("Submission payload must contain at least one answer"));
        }

        for (AnswerSubmissionDTO answer : answers) {
            String type = answer.getQuestionType() != null ? answer.getQuestionType().toUpperCase() : "";

            switch (type) {
                case "LIKERT":
                case "RATING_STARS":
                    if (answer.getNumericValue() == null || answer.getNumericValue() < 1.0 || answer.getNumericValue() > 5.0) {
                        return Mono.error(new InvalidAnswerException("Numeric score out of range (1-5) for LIKERT questionId: " + answer.getQuestionId()));
                    }
                    break;
                case "NPS":
                    if (answer.getNumericValue() == null || answer.getNumericValue() < 0.0 || answer.getNumericValue() > 10.0) {
                        return Mono.error(new InvalidAnswerException("Numeric score out of range (0-10) for NPS questionId: " + answer.getQuestionId()));
                    }
                    break;
                case "TEXT_OPEN":
                case "SHORT_TEXT":
                case "LONG_TEXT":
                    if (answer.getTextValue() == null && (answer.getSelectedOptions() == null || answer.getSelectedOptions().isEmpty())) {
                        log.debug("Open text value is null for questionId: {}", answer.getQuestionId());
                    }
                    break;
                case "MULTIPLE_CHOICE":
                case "SINGLE_CHOICE":
                    if ((answer.getSelectedOptions() == null || answer.getSelectedOptions().isEmpty()) && answer.getTextValue() == null) {
                        log.debug("Choice selection is empty for questionId: {}", answer.getQuestionId());
                    }
                    break;
            }
        }

        return Mono.empty();
    }
}
