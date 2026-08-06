package com.talnova.tesp.configservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Theme Color Contrast Accessibility Validation Request Payload")
public class ContrastValidationRequestDTO {

    @NotBlank(message = "primaryColor is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid HEX color format")
    @Schema(example = "#FFFFFF")
    private String primaryColor;

    @NotBlank(message = "backgroundColor is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid HEX color format")
    @Schema(example = "#000000")
    private String backgroundColor;

    public ContrastValidationRequestDTO() {
    }

    public ContrastValidationRequestDTO(String primaryColor, String backgroundColor) {
        this.primaryColor = primaryColor;
        this.backgroundColor = backgroundColor;
    }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public String getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }
}
