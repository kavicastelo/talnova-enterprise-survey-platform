package com.talnova.tesp.employeeservice.exception;

public class CustomAttributeTypeMismatchException extends RuntimeException {

    public CustomAttributeTypeMismatchException(String key, String expectedType, String actualValue) {
        super("Custom attribute type mismatch for key '" + key + "': expected " + expectedType + " value, received '" + actualValue + "'.");
    }
}
