package com.talnova.tesp.employeeservice;

import com.talnova.tesp.employeeservice.dto.AttributeDefinition;
import com.talnova.tesp.employeeservice.exception.CustomAttributeTypeMismatchException;
import com.talnova.tesp.employeeservice.validation.DynamicAttributeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DynamicAttributeValidatorTest {

    private DynamicAttributeValidator validator;
    private List<AttributeDefinition> schema;

    @BeforeEach
    void setUp() {
        validator = new DynamicAttributeValidator();
        schema = List.of(
                new AttributeDefinition("TenureYears", "Tenure Years", "NUMERIC", false, null),
                new AttributeDefinition("Gender", "Gender", "ENUM", false, List.of("Male", "Female", "Non-Binary", "Prefer Not To Say")),
                new AttributeDefinition("JoiningDate", "Joining Date", "DATE", false, null)
        );
    }

    @Test
    @DisplayName("TC-EMP-203-A: Valid demographic attributes pass validation cleanly FR-EMP-001")
    void testValidAttributesPassValidation() {
        Map<String, Object> attributes = Map.of(
                "TenureYears", 4.5,
                "Gender", "Female",
                "JoiningDate", "2022-03-15"
        );

        assertDoesNotThrow(() -> validator.validateAttributes(attributes, schema));
    }

    @Test
    @DisplayName("TC-EMP-203-B: Passing string 'Five Years' for NUMERIC attribute TenureYears throws CustomAttributeTypeMismatchException VR-EMP-004")
    void testInvalidNumericAttributeThrowsException() {
        Map<String, Object> invalidAttributes = Map.of(
                "TenureYears", "Five Years"
        );

        CustomAttributeTypeMismatchException exception = assertThrows(CustomAttributeTypeMismatchException.class,
                () -> validator.validateAttributes(invalidAttributes, schema));

        assertTrue(exception.getMessage().contains("TenureYears"));
        assertTrue(exception.getMessage().contains("NUMERIC"));
    }

    @Test
    @DisplayName("TC-EMP-203-C: Passing unpermitted option for ENUM attribute Gender throws CustomAttributeTypeMismatchException")
    void testInvalidEnumOptionThrowsException() {
        Map<String, Object> invalidAttributes = Map.of(
                "Gender", "UnknownOption"
        );

        CustomAttributeTypeMismatchException exception = assertThrows(CustomAttributeTypeMismatchException.class,
                () -> validator.validateAttributes(invalidAttributes, schema));

        assertTrue(exception.getMessage().contains("Gender"));
        assertTrue(exception.getMessage().contains("ENUM"));
    }
}
