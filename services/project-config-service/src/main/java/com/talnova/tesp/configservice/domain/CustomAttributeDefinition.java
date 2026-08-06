package com.talnova.tesp.configservice.domain;

import java.util.List;

public class CustomAttributeDefinition {
    private String key;
    private String displayName;
    private DataType dataType;
    private List<String> allowedValues;

    public CustomAttributeDefinition() {
    }

    public CustomAttributeDefinition(String key, String displayName, DataType dataType, List<String> allowedValues) {
        this.key = key;
        this.displayName = displayName;
        this.dataType = dataType;
        this.allowedValues = allowedValues;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public DataType getDataType() { return dataType; }
    public void setDataType(DataType dataType) { this.dataType = dataType; }

    public List<String> getAllowedValues() { return allowedValues; }
    public void setAllowedValues(List<String> allowedValues) { this.allowedValues = allowedValues; }

    public static class Builder {
        private String key;
        private String displayName;
        private DataType dataType;
        private List<String> allowedValues;

        public Builder key(String key) { this.key = key; return this; }
        public Builder displayName(String displayName) { this.displayName = displayName; return this; }
        public Builder dataType(DataType dataType) { this.dataType = dataType; return this; }
        public Builder allowedValues(List<String> allowedValues) { this.allowedValues = allowedValues; return this; }

        public CustomAttributeDefinition build() {
            return new CustomAttributeDefinition(key, displayName, dataType, allowedValues);
        }
    }
}
