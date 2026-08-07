package com.talnova.tesp.actionservice.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "action_templates")
public class ActionTemplateDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String templateId;

    @Indexed
    private String groupId;
    private String title;
    private String description;
    private String categoryName;
    private List<String> suggestedMilestones = new ArrayList<>();

    public ActionTemplateDocument() {}

    public ActionTemplateDocument(String id, String templateId, String groupId, String title, String description, String categoryName, List<String> suggestedMilestones) {
        this.id = id;
        this.templateId = templateId;
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.categoryName = categoryName;
        this.suggestedMilestones = suggestedMilestones != null ? suggestedMilestones : new ArrayList<>();
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<String> getSuggestedMilestones() { return suggestedMilestones; }
    public void setSuggestedMilestones(List<String> suggestedMilestones) { this.suggestedMilestones = suggestedMilestones; }

    public static class Builder {
        private String id;
        private String templateId;
        private String groupId;
        private String title;
        private String description;
        private String categoryName;
        private List<String> suggestedMilestones = new ArrayList<>();

        public Builder id(String id) { this.id = id; return this; }
        public Builder templateId(String templateId) { this.templateId = templateId; return this; }
        public Builder groupId(String groupId) { this.groupId = groupId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder suggestedMilestones(List<String> suggestedMilestones) { this.suggestedMilestones = suggestedMilestones; return this; }

        public ActionTemplateDocument build() {
            return new ActionTemplateDocument(id, templateId, groupId, title, description, categoryName, suggestedMilestones);
        }
    }
}
