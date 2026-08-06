package com.talnova.tesp.employeeservice.repository;

import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<EmployeeDocument, String>, EmployeeRepositoryCustom {

    boolean existsByProjectIdAndEmployeeIdAndIsDeletedFalse(String projectId, String employeeId);
}
