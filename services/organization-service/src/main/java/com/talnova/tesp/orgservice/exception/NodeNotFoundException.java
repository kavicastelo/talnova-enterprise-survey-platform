package com.talnova.tesp.orgservice.exception;

public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException(String nodeId) {
        super("Organization node not found for nodeId: " + nodeId);
    }
}
