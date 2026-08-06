package com.talnova.tesp.employeeservice.service;

import com.talnova.tesp.employeeservice.dto.CreateEmployeeDTO;
import com.talnova.tesp.employeeservice.dto.EmployeeResponseDTO;
import com.talnova.tesp.employeeservice.dto.UpdateEmployeeDTO;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(CreateEmployeeDTO dto);

    EmployeeResponseDTO getEmployeeByProjectIdAndEmployeeId(String projectId, String employeeId);

    EmployeeResponseDTO updateEmployee(String projectId, String employeeId, UpdateEmployeeDTO dto);

    EmployeeResponseDTO forgetEmployeeProfile(String projectId, String employeeId);
}
