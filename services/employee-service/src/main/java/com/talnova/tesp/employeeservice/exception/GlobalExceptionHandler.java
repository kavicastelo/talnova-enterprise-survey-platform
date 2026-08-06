package com.talnova.tesp.employeeservice.exception;

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

    @ExceptionHandler(DuplicateEmployeeIdException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateEmployeeId(DuplicateEmployeeIdException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("https://api.talnova.com/errors/duplicate-employee-id"));
        problem.setTitle("Duplicate Employee Identifier");
        problem.setProperty("correlationId", ProjectContextHolder.getCorrelationId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEmployeeNotFound(EmployeeNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://api.talnova.com/errors/employee-not-found"));
        problem.setTitle("Employee Profile Not Found");
        problem.setProperty("correlationId", ProjectContextHolder.getCorrelationId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(InvalidNodeAssignmentException.class)
    public ResponseEntity<ProblemDetail> handleInvalidNodeAssignment(InvalidNodeAssignmentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("https://api.talnova.com/errors/invalid-node-assignment"));
        problem.setTitle("Assigned Node ID Does Not Exist");
        problem.setProperty("correlationId", ProjectContextHolder.getCorrelationId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(CustomAttributeTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleAttributeTypeMismatch(CustomAttributeTypeMismatchException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("https://api.talnova.com/errors/custom-attribute-type-mismatch"));
        problem.setTitle("Custom Attribute Type Mismatch");
        problem.setProperty("correlationId", ProjectContextHolder.getCorrelationId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setType(URI.create("https://api.talnova.com/errors/access-denied"));
        problem.setTitle("Access Denied");
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
