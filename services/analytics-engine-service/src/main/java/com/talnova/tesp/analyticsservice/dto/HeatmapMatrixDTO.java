package com.talnova.tesp.analyticsservice.dto;

import java.util.List;

public class HeatmapMatrixDTO {

    private String campaignId;
    private String parentNodeId;
    private List<String> rowNodes;
    private List<String> columnThemes;
    private List<HeatmapCellDTO> cells;

    public HeatmapMatrixDTO() {}

    public HeatmapMatrixDTO(String campaignId, String parentNodeId, List<String> rowNodes, List<String> columnThemes, List<HeatmapCellDTO> cells) {
        this.campaignId = campaignId;
        this.parentNodeId = parentNodeId;
        this.rowNodes = rowNodes;
        this.columnThemes = columnThemes;
        this.cells = cells;
    }

    public static Builder builder() { return new Builder(); }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getParentNodeId() { return parentNodeId; }
    public void setParentNodeId(String parentNodeId) { this.parentNodeId = parentNodeId; }

    public List<String> getRowNodes() { return rowNodes; }
    public void setRowNodes(List<String> rowNodes) { this.rowNodes = rowNodes; }

    public List<String> getColumnThemes() { return columnThemes; }
    public void setColumnThemes(List<String> columnThemes) { this.columnThemes = columnThemes; }

    public List<HeatmapCellDTO> getCells() { return cells; }
    public void setCells(List<HeatmapCellDTO> cells) { this.cells = cells; }

    public static class Builder {
        private String campaignId;
        private String parentNodeId;
        private List<String> rowNodes;
        private List<String> columnThemes;
        private List<HeatmapCellDTO> cells;

        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder parentNodeId(String parentNodeId) { this.parentNodeId = parentNodeId; return this; }
        public Builder rowNodes(List<String> rowNodes) { this.rowNodes = rowNodes; return this; }
        public Builder columnThemes(List<String> columnThemes) { this.columnThemes = columnThemes; return this; }
        public Builder cells(List<HeatmapCellDTO> cells) { this.cells = cells; return this; }

        public HeatmapMatrixDTO build() {
            return new HeatmapMatrixDTO(campaignId, parentNodeId, rowNodes, columnThemes, cells);
        }
    }
}
