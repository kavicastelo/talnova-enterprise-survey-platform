package com.talnova.tesp.ingestionservice.exception;

import com.talnova.tesp.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ResponseIngestionExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ResponseIngestionExceptionHandler.class);

    @ExceptionHandler(InvalidTokenException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleInvalidTokenException(InvalidTokenException ex) {
        log.warn("Double-submission guard triggered: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), "ERR-TOKEN-403")));
    }

    @ExceptionHandler(InvalidAnswerException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleInvalidAnswerException(InvalidAnswerException ex) {
        log.warn("Answer AST validation failed: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), "ERR-ANS-400")));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidationException(WebExchangeBindException ex) {
        String errors = ex.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed for ingestion request: {}", errors);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed: " + errors, "ERR-VAL-400")));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument in ingestion request: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), "ERR-ARG-400")));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGenericException(Exception ex) {
        log.error("Unhandled error during response ingestion: ", ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error during response ingestion", "ERR-SYS-500")));
    }
}
