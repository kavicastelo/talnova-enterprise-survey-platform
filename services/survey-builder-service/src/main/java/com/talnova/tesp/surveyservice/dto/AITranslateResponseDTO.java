package com.talnova.tesp.surveyservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@Schema(description = "Response payload containing AI multi-language translations")
public class AITranslateResponseDTO {

    @Schema(description = "Map of target BCP-47 locale tags to translated prompt strings")
    private Map<String, String> translations = new HashMap<>();

    public AITranslateResponseDTO() {
    }

    public AITranslateResponseDTO(Map<String, String> translations) {
        this.translations = translations != null ? translations : new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, String> getTranslations() { return translations; }
    public void setTranslations(Map<String, String> translations) { this.translations = translations; }

    public static class Builder {
        private Map<String, String> translations = new HashMap<>();

        public Builder translations(Map<String, String> translations) { this.translations = translations; return this; }

        public AITranslateResponseDTO build() {
            return new AITranslateResponseDTO(translations);
        }
    }
}
