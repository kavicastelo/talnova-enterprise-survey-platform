package com.talnova.tesp.orgservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class NodeTypeValidator implements ConstraintValidator<ValidNodeType, String> {

    private static final Set<String> ALLOWED_NODE_TYPES = Set.of(
            "COMPANY",
            "SECTOR",
            "DIVISION",
            "BRANCH",
            "DEPARTMENT",
            "TEAM",
            "FUNCTION",
            "UNIT"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return ALLOWED_NODE_TYPES.contains(value.trim().toUpperCase());
    }
}
