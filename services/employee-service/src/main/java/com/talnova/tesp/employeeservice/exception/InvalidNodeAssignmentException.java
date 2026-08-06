package com.talnova.tesp.employeeservice.exception;

public class InvalidNodeAssignmentException extends RuntimeException {

    public InvalidNodeAssignmentException(String nodeId) {
        super("Assigned Node ID '" + nodeId + "' does not exist or is inactive in FEAT-002 organization hierarchy.");
    }
}
