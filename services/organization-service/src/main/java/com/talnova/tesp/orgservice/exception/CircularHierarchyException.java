package com.talnova.tesp.orgservice.exception;

public class CircularHierarchyException extends RuntimeException {
    public CircularHierarchyException(String message) {
        super(message);
    }
}
