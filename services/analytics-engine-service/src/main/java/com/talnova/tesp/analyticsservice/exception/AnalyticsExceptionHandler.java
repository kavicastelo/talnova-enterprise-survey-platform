package com.talnova.tesp.analyticsservice.exception;

import com.talnova.tesp.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AnalyticsExceptionHandler {

    @ExceptionHandler(UnauthorizedNodeScopeException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedNodeScope(UnauthorizedNodeScopeException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), "ERR-ANL-403");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(ExcessiveFilterParametersException.class)
    public ResponseEntity<ApiResponse<Void>> handleExcessiveFilters(ExcessiveFilterParametersException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), "ERR-ANL-400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
