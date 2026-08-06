package com.talnova.tesp.employeeservice.validation;

import com.talnova.tesp.employeeservice.dto.AttributeDefinition;
import com.talnova.tesp.employeeservice.exception.CustomAttributeTypeMismatchException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DynamicAttributeValidator {

    public void validateAttributes(Map<String, Object> attributes, List<AttributeDefinition> definitions) {
        if (attributes == null || attributes.isEmpty() || definitions == null || definitions.isEmpty()) {
            return;
        }

        Map<String, AttributeDefinition> defMap = definitions.stream()
                .collect(java.util.stream.Collectors.toMap(AttributeDefinition::getKey, d -> d, (a, b) -> a));

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            AttributeDefinition def = defMap.get(key);
            if (def == null) {
                continue; // Permissive for ad-hoc custom keys unless strict schema enforced
            }

            String expectedType = def.getDataType() != null ? def.getDataType().toUpperCase() : "STRING";
            String valStr = value.toString();

            switch (expectedType) {
                case "NUMERIC":
                    if (!(value instanceof Number)) {
                        try {
                            Double.parseDouble(valStr);
                        } catch (NumberFormatException ex) {
                            throw new CustomAttributeTypeMismatchException(key, "NUMERIC", valStr);
                        }
                    }
                    break;

                case "ENUM":
                    if (def.getPermittedValues() != null && !def.getPermittedValues().isEmpty()) {
                        if (!def.getPermittedValues().contains(valStr)) {
                            throw new CustomAttributeTypeMismatchException(key, "ENUM (Permitted: " + def.getPermittedValues() + ")", valStr);
                        }
                    }
                    break;

                case "DATE":
                    // Validate basic date format string if string
                    if (value instanceof String) {
                        if (!valStr.matches("^\\d{4}-\\d{2}-\\d{2}.*")) {
                            throw new CustomAttributeTypeMismatchException(key, "DATE (YYYY-MM-DD)", valStr);
                        }
                    }
                    break;

                default: // STRING
                    break;
            }
        }
    }
}
