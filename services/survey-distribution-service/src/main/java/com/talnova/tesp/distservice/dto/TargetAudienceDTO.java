package com.talnova.tesp.distservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "DTO for campaign target audience selection")
public class TargetAudienceDTO {

    @Schema(description = "Organization Node sub-tree materialized paths", example = "[\"N-301\", \"N-302\"]")
    private List<String> nodeIds = new ArrayList<>();

    @Schema(description = "Demographic custom attribute filter criteria")
    private Map<String, Object> demographicFilters = new HashMap<>();

    public TargetAudienceDTO() {
    }

    public TargetAudienceDTO(List<String> nodeIds, Map<String, Object> demographicFilters) {
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

        public TargetAudienceDTO build() {
            return new TargetAudienceDTO(nodeIds, demographicFilters);
        }
    }
}
