package com.talnova.tesp.orgservice.exception;

public class DuplicateNodeIdException extends RuntimeException {
    public DuplicateNodeIdException(String nodeId) {
        super("An organization node with ID '" + nodeId + "' already exists in this project workspace.");
    }
}
