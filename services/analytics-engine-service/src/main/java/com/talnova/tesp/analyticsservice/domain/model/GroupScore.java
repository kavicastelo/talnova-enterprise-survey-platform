package com.talnova.tesp.analyticsservice.domain.model;

public class GroupScore {

    private String groupId;
    private Double score;

    public GroupScore() {}

    public GroupScore(String groupId, Double score) {
        this.groupId = groupId;
        this.score = score;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public static class Builder {
        private String groupId;
        private Double score;

        public Builder groupId(String groupId) { this.groupId = groupId; return this; }
        public Builder score(Double score) { this.score = score; return this; }

        public GroupScore build() {
            return new GroupScore(groupId, score);
        }
    }
}
