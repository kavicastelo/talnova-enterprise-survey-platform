package com.talnova.tesp.actionservice.domain.model;

import java.time.Instant;

public class ActionMilestone {

    private String milestoneId;
    private String title;
    private boolean completed;
    private Instant dueDate;
    private String assigneeId;

    public ActionMilestone() {}

    public ActionMilestone(String milestoneId, String title, boolean completed, Instant dueDate, String assigneeId) {
        this.milestoneId = milestoneId;
        this.title = title;
        this.completed = completed;
        this.dueDate = dueDate;
        this.assigneeId = assigneeId;
    }

    public static Builder builder() { return new Builder(); }

    public String getMilestoneId() { return milestoneId; }
    public void setMilestoneId(String milestoneId) { this.milestoneId = milestoneId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Instant getDueDate() { return dueDate; }
    public void setDueDate(Instant dueDate) { this.dueDate = dueDate; }

    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }

    public static class Builder {
        private String milestoneId;
        private String title;
        private boolean completed;
        private Instant dueDate;
        private String assigneeId;

        public Builder milestoneId(String milestoneId) { this.milestoneId = milestoneId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder completed(boolean completed) { this.completed = completed; return this; }
        public Builder dueDate(Instant dueDate) { this.dueDate = dueDate; return this; }
        public Builder assigneeId(String assigneeId) { this.assigneeId = assigneeId; return this; }

        public ActionMilestone build() {
            return new ActionMilestone(milestoneId, title, completed, dueDate, assigneeId);
        }
    }
}
