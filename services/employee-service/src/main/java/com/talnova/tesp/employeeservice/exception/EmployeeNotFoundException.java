package com.talnova.tesp.employeeservice.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String employeeId) {
        super("Employee profile with employeeId '" + employeeId + "' was not found.");
    }
}
