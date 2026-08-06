package com.talnova.tesp.employeeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "AI Header Mapping Request Payload")
public class HeaderMappingRequestDTO {

    @NotEmpty(message = "headers list cannot be empty")
    private List<String> headers;

    public HeaderMappingRequestDTO() {
    }

    public HeaderMappingRequestDTO(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getHeaders() { return headers; }
    public void setHeaders(List<String> headers) { this.headers = headers; }
}
