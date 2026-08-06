package com.talnova.tesp.distservice.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetAudience {
    private List<String> nodeIds = new ArrayList<>();
    private Map<String, Object> demographicFilters = new HashMap<>();

    public TargetAudience() {
    }

    public TargetAudience(List<String> nodeIds, Map<String, Object> demographicFilters) {
        this.nodeIds = nodeIds != null ? nodeIds : new ArrayList<>();
        this.demographicFilters = demographicFilters != null ? demographicFilters : new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getNodeIds() { return nodeIds; }
    public void setNodeIds(List<String> nodeIds) { this.nodeIds = nodeIds; }

    public Map<String, Object> getDemographicFilters() { return demographicFilters; }
    public void setDemographicFilters(Map<String, Object> demographicFilters) { this.demographicFilters = demographicFilters; }

    public static class Builder {
        private List<String> nodeIds = new ArrayList<>();
        private Map<String, Object> demographicFilters = new HashMap<>();

        public Builder nodeIds(List<String> nodeIds) { this.nodeIds = nodeIds; return this; }
        public Builder demographicFilters(Map<String, Object> demographicFilters) { this.demographicFilters = demographicFilters; return this; }

        public TargetAudience build() {
            return new TargetAudience(nodeIds, demographicFilters);
        }
    }
}
