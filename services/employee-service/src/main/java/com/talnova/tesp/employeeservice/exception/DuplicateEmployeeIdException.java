package com.talnova.tesp.employeeservice.exception;

public class DuplicateEmployeeIdException extends RuntimeException {

    public DuplicateEmployeeIdException(String employeeId) {
        super("Employee profile with employeeId '" + employeeId + "' already exists in this project workspace.");
    }
}
