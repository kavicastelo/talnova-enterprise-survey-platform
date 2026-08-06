package com.talnova.tesp.ingestionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Async response DTO returned upon survey response intake")
public class IngestionResponseDTO {

    private String responseId;
    private String status;
    private String message;
    private Instant timestamp;

    public IngestionResponseDTO() {
        this.timestamp = Instant.now();
    }

    public IngestionResponseDTO(String responseId, String status, String message, Instant timestamp) {
        this.responseId = responseId;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String responseId;
        private String status;
        private String message;
        private Instant timestamp = Instant.now();

        public Builder responseId(String responseId) { this.responseId = responseId; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public IngestionResponseDTO build() {
            return new IngestionResponseDTO(responseId, status, message, timestamp);
        }
    }
}
