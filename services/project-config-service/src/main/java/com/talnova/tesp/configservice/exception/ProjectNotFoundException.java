package com.talnova.tesp.configservice.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String projectId) {
        super("Project workspace not found for projectId: " + projectId);
    }
}
