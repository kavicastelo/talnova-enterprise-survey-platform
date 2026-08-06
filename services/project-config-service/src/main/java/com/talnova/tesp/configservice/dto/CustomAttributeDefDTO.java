package com.talnova.tesp.configservice.dto;

import com.talnova.tesp.configservice.domain.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Custom Demographic Attribute Definition Payload")
public class CustomAttributeDefDTO {

    @NotBlank(message = "Attribute key is required")
    @Schema(example = "tenure")
    private String key;

    @NotBlank(message = "Display name is required")
    @Schema(example = "Employee Tenure")
    private String displayName;

    @NotNull(message = "Data type is required")
    @Schema(example = "STRING")
    private DataType dataType;

    @Schema(example = "[\"<1 Year\", \"1-3 Years\", \"3-5 Years\", \"5+ Years\"]")
    private List<String> allowedValues;

    public CustomAttributeDefDTO() {
    }

    public CustomAttributeDefDTO(String key, String displayName, DataType dataType, List<String> allowedValues) {
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

        public CustomAttributeDefDTO build() {
            return new CustomAttributeDefDTO(key, displayName, dataType, allowedValues);
        }
    }
}
