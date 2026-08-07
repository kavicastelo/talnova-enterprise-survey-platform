package com.talnova.tesp.analyticsservice.scoring;

public class EnpsResult {

    private final int totalResponses;
    private final int promoterCount;
    private final int passiveCount;
    private final int detractorCount;
    private final double promoterPercentage;
    private final double detractorPercentage;
    private final double enpsScore;

    public EnpsResult(int totalResponses, int promoterCount, int passiveCount, int detractorCount, double promoterPercentage, double detractorPercentage, double enpsScore) {
        this.totalResponses = totalResponses;
        this.promoterCount = promoterCount;
        this.passiveCount = passiveCount;
        this.detractorCount = detractorCount;
        this.promoterPercentage = promoterPercentage;
        this.detractorPercentage = detractorPercentage;
        this.enpsScore = enpsScore;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getTotalResponses() { return totalResponses; }
    public int getPromoterCount() { return promoterCount; }
    public int getPassiveCount() { return passiveCount; }
    public int getDetractorCount() { return detractorCount; }
    public double getPromoterPercentage() { return promoterPercentage; }
    public double getDetractorPercentage() { return detractorPercentage; }
    public double getEnpsScore() { return enpsScore; }

    public static class Builder {
        private int totalResponses;
        private int promoterCount;
        private int passiveCount;
        private int detractorCount;
        private double promoterPercentage;
        private double detractorPercentage;
        private double enpsScore;

        public Builder totalResponses(int totalResponses) { this.totalResponses = totalResponses; return this; }
        public Builder promoterCount(int promoterCount) { this.promoterCount = promoterCount; return this; }
        public Builder passiveCount(int passiveCount) { this.passiveCount = passiveCount; return this; }
        public Builder detractorCount(int detractorCount) { this.detractorCount = detractorCount; return this; }
        public Builder promoterPercentage(double promoterPercentage) { this.promoterPercentage = promoterPercentage; return this; }
        public Builder detractorPercentage(double detractorPercentage) { this.detractorPercentage = detractorPercentage; return this; }
        public Builder enpsScore(double enpsScore) { this.enpsScore = enpsScore; return this; }

        public EnpsResult build() {
            return new EnpsResult(totalResponses, promoterCount, passiveCount, detractorCount, promoterPercentage, detractorPercentage, enpsScore);
        }
    }
}
