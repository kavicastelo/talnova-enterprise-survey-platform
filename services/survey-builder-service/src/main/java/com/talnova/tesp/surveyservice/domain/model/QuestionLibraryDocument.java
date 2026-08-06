package com.talnova.tesp.surveyservice.domain.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MongoDB Document entity representing a reusable Question Library item catalog.
 */
@Document(collection = "question_library")
public class QuestionLibraryDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String libraryId;

    private String category;

    private QuestionType type;

    private String groupId;

    private Map<String, String> prompt = new HashMap<>();

    private List<String> tags = new ArrayList<>();

    private boolean isDeleted;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public QuestionLibraryDocument() {
    }

    public QuestionLibraryDocument(String id, String libraryId, String category, QuestionType type, String groupId,
                                   Map<String, String> prompt, List<String> tags, boolean isDeleted,
                                   Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.libraryId = libraryId;
        this.category = category;
        this.type = type;
        this.groupId = groupId;
        this.prompt = prompt != null ? prompt : new HashMap<>();
        this.tags = tags != null ? tags : new ArrayList<>();
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private String id;
        private String libraryId;
        private String category;
        private QuestionType type;
        private String groupId;
        private Map<String, String> prompt = new HashMap<>();
        private List<String> tags = new ArrayList<>();
        private boolean isDeleted;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder libraryId(String libraryId) { this.libraryId = libraryId; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder type(QuestionType type) { this.type = type; return this; }
        public Builder groupId(String groupId) { this.groupId = groupId; return this; }
        public Builder prompt(Map<String, String> prompt) { this.prompt = prompt; return this; }
        public Builder tags(List<String> tags) { this.tags = tags; return this; }
        public Builder isDeleted(boolean isDeleted) { this.isDeleted = isDeleted; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public QuestionLibraryDocument build() {
            return new QuestionLibraryDocument(id, libraryId, category, type, groupId, prompt, tags, isDeleted, createdAt, updatedAt);
        }
    }
}
