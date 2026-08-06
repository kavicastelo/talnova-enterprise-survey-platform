package com.talnova.tesp.configservice.exception;

public class DuplicateProjectIdException extends RuntimeException {
    public DuplicateProjectIdException(String projectId) {
        super("Project workspace with projectId " + projectId + " already exists.");
    }
}
