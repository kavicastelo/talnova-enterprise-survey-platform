package com.talnova.tesp.employeeservice.repository;

import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepositoryCustom {

    Optional<EmployeeDocument> findActiveEmployeeByEmployeeId(String projectId, String employeeId);

    List<EmployeeDocument> findActiveByNodeIdAndStatus(String projectId, String nodeId, EmployeeStatus status);

    List<EmployeeDocument> findActiveByMatrixNodeId(String projectId, String matrixNodeId);
}
