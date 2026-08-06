package com.talnova.tesp.employeeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "AI Header Mapping Response Payload")
public class HeaderMappingResponseDTO {

    private List<HeaderMapping> mappings;

    public HeaderMappingResponseDTO() {
    }

    public HeaderMappingResponseDTO(List<HeaderMapping> mappings) {
        this.mappings = mappings;
    }

    public List<HeaderMapping> getMappings() { return mappings; }
    public void setMappings(List<HeaderMapping> mappings) { this.mappings = mappings; }
}
