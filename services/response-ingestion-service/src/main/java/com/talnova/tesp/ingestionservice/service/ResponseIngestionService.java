package com.talnova.tesp.ingestionservice.service;

import com.talnova.tesp.ingestionservice.dto.IngestionResponseDTO;
import com.talnova.tesp.ingestionservice.dto.ResponseSubmissionDTO;
import reactor.core.publisher.Mono;

public interface ResponseIngestionService {

    Mono<IngestionResponseDTO> ingestResponse(ResponseSubmissionDTO submission);
}
