package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.validation.NodeTypeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class NodeTypeValidatorTest {

    private NodeTypeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NodeTypeValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"COMPANY", "SECTOR", "DIVISION", "BRANCH", "DEPARTMENT", "TEAM", "FUNCTION", "UNIT", "department", "company"})
    @DisplayName("TC-ORG-203-A: Valid enterprise node types pass validation")
    void testValidNodeTypesPass(String validObjectType) {
        assertTrue(validator.isValid(validObjectType, null));
    }

    @Test
    @DisplayName("TC-ORG-203-B: Unrecognized node type returns false VR-ORG-003")
    void testUnrecognizedNodeTypeFails() {
        assertFalse(validator.isValid("REGIONAL_HUB", null));
        assertFalse(validator.isValid("INVALID_TYPE", null));
    }

    @Test
    @DisplayName("TC-ORG-203-C: Null or blank node type returns false")
    void testNullOrBlankFails() {
        assertFalse(validator.isValid(null, null));
        assertFalse(validator.isValid("   ", null));
    }
}
