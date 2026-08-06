package com.talnova.tesp.surveyservice.dto;

import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "Request payload for creating a Question Library template entry")
public class QuestionLibraryCreateDTO {

    @NotBlank(message = "libraryId is mandatory")
    @Schema(description = "Unique library item identifier", example = "LIB-LEAD-001")
    private String libraryId;

    @NotBlank(message = "category is mandatory")
    @Schema(description = "Library domain category", example = "LEADERSHIP")
    private String category;

    @NotNull(message = "type is mandatory")
    @Schema(description = "Question type enum", example = "LIKERT")
    private QuestionType type;

    @NotBlank(message = "groupId is mandatory")
    @Schema(description = "Analytic theme question group ID", example = "GRP-LEADERSHIP")
    private String groupId;

    @Schema(description = "Localized question prompt map", example = "{\"en-US\": \"My direct manager provides clear feedback.\"}")
    private Map<String, String> prompt = new HashMap<>();

    @Schema(description = "Filter and taxonomy tags", example = "[\"management\", \"feedback\"]")
    private List<String> tags = new ArrayList<>();

    public QuestionLibraryCreateDTO() {
    }

    public QuestionLibraryCreateDTO(String libraryId, String category, QuestionType type, String groupId,
                                    Map<String, String> prompt, List<String> tags) {
        this.libraryId = libraryId;
        this.category = category;
        this.type = type;
        this.groupId = groupId;
        this.prompt = prompt != null ? prompt : new HashMap<>();
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getLibraryId() { return libraryId; }
    public void setLibraryId(String libraryId) { this.libraryId = libraryId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public Map<String, String> getPrompt() { return prompt; }
    public void setPrompt(Map<String, String> prompt) { this.prompt = prompt; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public static class Builder {
        private String libraryId;
        private String category;
        private QuestionType type;
        private String groupId;
        private Map<String, String> prompt = new HashMap<>();
        private List<String> tags = new ArrayList<>();

        public Builder libraryId(String libraryId) { this.libraryId = libraryId; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder type(QuestionType type) { this.type = type; return this; }
        public Builder groupId(String groupId) { this.groupId = groupId; return this; }
        public Builder prompt(Map<String, String> prompt) { this.prompt = prompt; return this; }
        public Builder tags(List<String> tags) { this.tags = tags; return this; }

        public QuestionLibraryCreateDTO build() {
            return new QuestionLibraryCreateDTO(libraryId, category, type, groupId, prompt, tags);
        }
    }
}
