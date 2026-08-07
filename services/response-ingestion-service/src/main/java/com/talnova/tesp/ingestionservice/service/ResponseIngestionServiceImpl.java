package com.talnova.tesp.ingestionservice.service;

import com.talnova.tesp.ingestionservice.domain.model.AnswerItem;
import com.talnova.tesp.ingestionservice.domain.model.SurveyResponseDocument;
import com.talnova.tesp.ingestionservice.dto.AnswerSubmissionDTO;
import com.talnova.tesp.ingestionservice.dto.IngestionResponseDTO;
import com.talnova.tesp.ingestionservice.dto.ResponseSubmissionDTO;
import com.talnova.tesp.ingestionservice.event.ReactiveKafkaResponseProducer;
import com.talnova.tesp.ingestionservice.event.SurveyResponseSubmittedEvent;
import com.talnova.tesp.ingestionservice.exception.InvalidTokenException;
import com.talnova.tesp.ingestionservice.repository.ResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResponseIngestionServiceImpl implements ResponseIngestionService {

    private static final Logger log = LoggerFactory.getLogger(ResponseIngestionServiceImpl.class);

    private final ResponseRepository responseRepository;
    private final AtomicTokenBurnerService tokenBurnerService;
    private final AnswerAstValidatorService answerAstValidatorService;
    private final DemographicSnapshotEmbedderService demographicSnapshotEmbedderService;
    private final ReactiveKafkaResponseProducer kafkaResponseProducer;
    private final PiiScrubberService piiScrubberService;
    private final XssSanitizerService xssSanitizerService;

    public ResponseIngestionServiceImpl(ResponseRepository responseRepository,
                                         AtomicTokenBurnerService tokenBurnerService,
                                         AnswerAstValidatorService answerAstValidatorService,
                                         DemographicSnapshotEmbedderService demographicSnapshotEmbedderService,
                                         ReactiveKafkaResponseProducer kafkaResponseProducer,
                                         PiiScrubberService piiScrubberService,
                                         XssSanitizerService xssSanitizerService) {
        this.responseRepository = responseRepository;
        this.tokenBurnerService = tokenBurnerService;
        this.answerAstValidatorService = answerAstValidatorService;
        this.demographicSnapshotEmbedderService = demographicSnapshotEmbedderService;
        this.kafkaResponseProducer = kafkaResponseProducer;
        this.piiScrubberService = piiScrubberService;
        this.xssSanitizerService = xssSanitizerService;
    }

    @Override
    public Mono<IngestionResponseDTO> ingestResponse(ResponseSubmissionDTO submission) {
        log.info("Processing reactive response submission for campaignId: {}, surveyId: {}, mode: {}",
                submission.getCampaignId(), submission.getSurveyId(), submission.getRespondentType());

        if (submission.getProjectId() == null || submission.getProjectId().isBlank()) {
            return Mono.error(new com.talnova.tesp.ingestionservice.exception.InvalidAnswerException("projectId is mandatory"));
        }

        return answerAstValidatorService.validateAnswers(submission)
                .then(Mono.defer(() -> {
                    Mono<String> tokenValidationMono;
                    if (submission.getResponseToken() != null && !submission.getResponseToken().isBlank()) {
                        tokenValidationMono = tokenBurnerService.burnToken(submission.getResponseToken())
                                .switchIfEmpty(Mono.error(new InvalidTokenException("Invalid or expired survey token")));
                    } else {
                        tokenValidationMono = Mono.just("");
                    }

                    return tokenValidationMono.flatMap(tokenMetadata -> {
                        Map<String, String> snapshot = demographicSnapshotEmbedderService.embedDemographicSnapshot(tokenMetadata, submission.getRespondentType());
                        String generatedId = "RSP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                        SurveyResponseDocument document = SurveyResponseDocument.builder()
                                .id(generatedId)
                                .projectId(submission.getProjectId())
                                .campaignId(submission.getCampaignId())
                                .surveyId(submission.getSurveyId())
                                .surveyVersion(submission.getSurveyVersion())
                                .respondentType(submission.getRespondentType())
                                .responseToken(submission.getResponseToken())
                                .nodeId(submission.getNodeId())
                                .demographicSnapshot(snapshot)
                                .answers(submission.getAnswers().stream()
                                        .map(this::mapAndSanitizeAnswerItem)
                                        .collect(Collectors.toList()))
                                .submittedAt(Instant.now())
                                .isDeleted(false)
                                .build();

                        return responseRepository.save(document)
                                .doOnSuccess(saved -> log.info("Successfully ingested survey response document '{}' in MongoDB", saved.getId()))
                                .flatMap(saved -> {
                                    SurveyResponseSubmittedEvent event = SurveyResponseSubmittedEvent.builder()
                                            .eventId("EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                                            .projectId(saved.getProjectId())
                                            .campaignId(saved.getCampaignId())
                                            .surveyId(saved.getSurveyId())
                                            .responseId(saved.getId())
                                            .respondentType(saved.getRespondentType())
                                            .nodeId(saved.getNodeId())
                                            .answerCount(saved.getAnswers().size())
                                            .timestamp(Instant.now())
                                            .build();

                                    return kafkaResponseProducer.publishResponseEvent(event)
                                            .thenReturn(IngestionResponseDTO.builder()
                                                    .responseId(saved.getId())
                                                    .status("ACCEPTED")
                                                    .message("Survey response submitted successfully")
                                                    .timestamp(Instant.now())
                                                    .build());
                                });
                    });
                }));
    }

    private AnswerItem mapAndSanitizeAnswerItem(AnswerSubmissionDTO dto) {
        String xssCleanText = xssSanitizerService.sanitizeText(dto.getTextValue());
        String piiCleanText = piiScrubberService.sanitizeOpenText(xssCleanText);

        return AnswerItem.builder()
                .questionId(dto.getQuestionId())
                .questionType(dto.getQuestionType())
                .numericValue(dto.getNumericValue())
                .textValue(piiCleanText)
                .selectedOptions(dto.getSelectedOptions())
                .build();
    }
}
