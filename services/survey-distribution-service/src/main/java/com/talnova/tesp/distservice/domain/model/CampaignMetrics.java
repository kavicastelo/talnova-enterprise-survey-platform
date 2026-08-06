package com.talnova.tesp.distservice.domain.model;

public class CampaignMetrics {
    private int totalTargeted;
    private int sent;
    private int delivered;
    private int opened;
    private int started;
    private int completed;
    private int bounced;

    public CampaignMetrics() {
    }

    public CampaignMetrics(int totalTargeted, int sent, int delivered, int opened, int started, int completed, int bounced) {
        this.totalTargeted = totalTargeted;
        this.sent = sent;
        this.delivered = delivered;
        this.opened = opened;
        this.started = started;
        this.completed = completed;
        this.bounced = bounced;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getTotalTargeted() { return totalTargeted; }
    public void setTotalTargeted(int totalTargeted) { this.totalTargeted = totalTargeted; }

    public int getSent() { return sent; }
    public void setSent(int sent) { this.sent = sent; }

    public int getDelivered() { return delivered; }
    public void setDelivered(int delivered) { this.delivered = delivered; }

    public int getOpened() { return opened; }
    public void setOpened(int opened) { this.opened = opened; }

    public int getStarted() { return started; }
    public void setStarted(int started) { this.started = started; }

    public int getCompleted() { return completed; }
    public void setCompleted(int completed) { this.completed = completed; }

    public int getBounced() { return bounced; }
    public void setBounced(int bounced) { this.bounced = bounced; }

    public static class Builder {
        private int totalTargeted;
        private int sent;
        private int delivered;
        private int opened;
        private int started;
        private int completed;
        private int bounced;

        public Builder totalTargeted(int totalTargeted) { this.totalTargeted = totalTargeted; return this; }
        public Builder sent(int sent) { this.sent = sent; return this; }
        public Builder delivered(int delivered) { this.delivered = delivered; return this; }
        public Builder opened(int opened) { this.opened = opened; return this; }
        public Builder started(int started) { this.started = started; return this; }
        public Builder completed(int completed) { this.completed = completed; return this; }
        public Builder bounced(int bounced) { this.bounced = bounced; return this; }

        public CampaignMetrics build() {
            return new CampaignMetrics(totalTargeted, sent, delivered, opened, started, completed, bounced);
        }
    }
}
