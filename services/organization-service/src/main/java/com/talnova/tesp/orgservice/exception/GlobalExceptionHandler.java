package com.talnova.tesp.orgservice.exception;

import com.talnova.tesp.common.context.ProjectContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateNodeIdException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateNodeId(DuplicateNodeIdException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("https://api.talnova.com/errors/duplicate-node-id"));
        problem.setTitle("Duplicate Node Identifier");
        problem.setProperty("correlationId", ProjectContextHolder.getCorrelationId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNodeNotFound(NodeNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://api.talnova.com/errors/node-not-found"));
        problem.setTitle("Organization Node Not Found");
        problem.setProperty("correlationId", ProjectContextHolder.getCorrelationId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(CircularHierarchyException.class)
    public ResponseEntity<ProblemDetail> handleCircularHierarchy(CircularHierarchyException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create("https://api.talnova.com/errors/circular-hierarchy-detected"));
        problem.setTitle("Circular Hierarchy DAG Cycle Detected");
        problem.setProperty("correlationId", ProjectContextHolder.getCorrelationId());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setType(URI.create("https://api.talnova.com/errors/abac-scope-access-denied"));
        problem.setTitle("Sub-Tree Scope Access Denied");
        problem.setProperty("correlationId", ProjectContextHolder.getCorrelationId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setType(URI.create("https://api.talnova.com/errors/validation-error"));
        problem.setTitle("Payload Validation Error");
        problem.setProperty("correlationId", ProjectContextHolder.getCorrelationId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }
}
