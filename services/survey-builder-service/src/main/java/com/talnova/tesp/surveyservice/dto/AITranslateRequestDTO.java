package com.talnova.tesp.surveyservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Request payload for AI one-click multi-language translation")
public class AITranslateRequestDTO {

    @NotBlank(message = "sourceText is mandatory")
    @Schema(description = "Source text to translate", example = "My direct manager provides clear feedback.")
    private String sourceText;

    @NotBlank(message = "sourceLocale is mandatory")
    @Schema(description = "Source BCP-47 locale tag", example = "en-US")
    private String sourceLocale;

    @NotEmpty(message = "targetLocales list must not be empty")
    @Schema(description = "Target BCP-47 locale tags for translation", example = "[\"si-LK\", \"ta-LK\", \"es-ES\"]")
    private List<String> targetLocales = new ArrayList<>();

    public AITranslateRequestDTO() {
    }

    public AITranslateRequestDTO(String sourceText, String sourceLocale, List<String> targetLocales) {
        this.sourceText = sourceText;
        this.sourceLocale = sourceLocale;
        this.targetLocales = targetLocales != null ? targetLocales : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSourceText() { return sourceText; }
    public void setSourceText(String sourceText) { this.sourceText = sourceText; }

    public String getSourceLocale() { return sourceLocale; }
    public void setSourceLocale(String sourceLocale) { this.sourceLocale = sourceLocale; }

    public List<String> getTargetLocales() { return targetLocales; }
    public void setTargetLocales(List<String> targetLocales) { this.targetLocales = targetLocales; }

    public static class Builder {
        private String sourceText;
        private String sourceLocale;
        private List<String> targetLocales = new ArrayList<>();

        public Builder sourceText(String sourceText) { this.sourceText = sourceText; return this; }
        public Builder sourceLocale(String sourceLocale) { this.sourceLocale = sourceLocale; return this; }
        public Builder targetLocales(List<String> targetLocales) { this.targetLocales = targetLocales; return this; }

        public AITranslateRequestDTO build() {
            return new AITranslateRequestDTO(sourceText, sourceLocale, targetLocales);
        }
    }
}
