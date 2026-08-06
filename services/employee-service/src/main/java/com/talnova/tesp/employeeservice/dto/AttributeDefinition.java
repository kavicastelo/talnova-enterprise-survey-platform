package com.talnova.tesp.employeeservice.dto;

import java.util.List;

public class AttributeDefinition {

    private String key;
    private String label;
    private String dataType; // STRING, NUMERIC, ENUM, DATE
    private Boolean isRequired;
    private List<String> permittedValues;

    public AttributeDefinition() {
    }

    public AttributeDefinition(String key, String label, String dataType, Boolean isRequired, List<String> permittedValues) {
        this.key = key;
        this.label = label;
        this.dataType = dataType;
        this.isRequired = isRequired;
        this.permittedValues = permittedValues;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }

    public List<String> getPermittedValues() { return permittedValues; }
    public void setPermittedValues(List<String> permittedValues) { this.permittedValues = permittedValues; }
}
