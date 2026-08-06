package com.talnova.tesp.employeeservice.service;

import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import com.talnova.tesp.employeeservice.dto.AttributeDefinition;
import com.talnova.tesp.employeeservice.dto.CreateEmployeeDTO;
import com.talnova.tesp.employeeservice.dto.EmployeeResponseDTO;
import com.talnova.tesp.employeeservice.dto.UpdateEmployeeDTO;
import com.talnova.tesp.employeeservice.exception.DuplicateEmployeeIdException;
import com.talnova.tesp.employeeservice.exception.EmployeeNotFoundException;
import com.talnova.tesp.employeeservice.mapper.EmployeeMapper;
import com.talnova.tesp.employeeservice.repository.EmployeeRepository;
import com.talnova.tesp.employeeservice.validation.DynamicAttributeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;
    private final DynamicAttributeValidator attributeValidator;

    public EmployeeServiceImpl(EmployeeRepository repository,
                               EmployeeMapper mapper,
                               DynamicAttributeValidator attributeValidator) {
        this.repository = repository;
        this.mapper = mapper;
        this.attributeValidator = attributeValidator;
    }

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(CreateEmployeeDTO dto) {
        if (repository.existsByProjectIdAndEmployeeIdAndIsDeletedFalse(dto.getProjectId(), dto.getEmployeeId())) {
            throw new DuplicateEmployeeIdException(dto.getEmployeeId());
        }

        List<AttributeDefinition> defaultSchema = List.of(
                new AttributeDefinition("TenureYears", "Tenure Years", "NUMERIC", false, null),
                new AttributeDefinition("Gender", "Gender", "ENUM", false, List.of("Male", "Female", "Non-Binary", "Prefer Not To Say")),
                new AttributeDefinition("JoiningDate", "Joining Date", "DATE", false, null)
        );

        attributeValidator.validateAttributes(dto.getAttributes(), defaultSchema);

        EmployeeDocument doc = mapper.toDocument(dto);
        EmployeeDocument savedDoc = repository.save(doc);

        return mapper.toResponseDTO(savedDoc);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeByProjectIdAndEmployeeId(String projectId, String employeeId) {
        EmployeeDocument doc = repository.findActiveEmployeeByEmployeeId(projectId, employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return mapper.toResponseDTO(doc);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(String projectId, String employeeId, UpdateEmployeeDTO dto) {
        EmployeeDocument existingDoc = repository.findActiveEmployeeByEmployeeId(projectId, employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (dto.getAttributes() != null) {
            List<AttributeDefinition> defaultSchema = List.of(
                    new AttributeDefinition("TenureYears", "Tenure Years", "NUMERIC", false, null),
                    new AttributeDefinition("Gender", "Gender", "ENUM", false, List.of("Male", "Female", "Non-Binary", "Prefer Not To Say")),
                    new AttributeDefinition("JoiningDate", "Joining Date", "DATE", false, null)
            );
            attributeValidator.validateAttributes(dto.getAttributes(), defaultSchema);
            existingDoc.setAttributes(dto.getAttributes());
        }

        if (dto.getEmail() != null) {
            existingDoc.setEmail(dto.getEmail());
        }
        if (dto.getFullName() != null) {
            existingDoc.setFullName(dto.getFullName());
        }
        if (dto.getPhoneNumber() != null) {
            existingDoc.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getNodeId() != null) {
            existingDoc.setNodeId(dto.getNodeId());
        }
        if (dto.getMatrixNodeIds() != null) {
            existingDoc.setMatrixNodeIds(dto.getMatrixNodeIds());
        }
        if (dto.getStatus() != null) {
            existingDoc.setStatus(dto.getStatus());
        }

        EmployeeDocument savedDoc = repository.save(existingDoc);
        return mapper.toResponseDTO(savedDoc);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO forgetEmployeeProfile(String projectId, String employeeId) {
        EmployeeDocument existingDoc = repository.findActiveEmployeeByEmployeeId(projectId, employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        // GDPR Right-to-be-Forgotten PII scrambler
        existingDoc.setEmail("anonymized-" + UUID.randomUUID().toString().substring(0, 8) + "@deleted.local");
        existingDoc.setFullName("ANONYMIZED_EMPLOYEE");
        existingDoc.setPhoneNumber("+00000000000");
        existingDoc.setAttributes(Map.of());
        existingDoc.setStatus(EmployeeStatus.TERMINATED);
        existingDoc.setIsDeleted(true);

        EmployeeDocument savedDoc = repository.save(existingDoc);
        return mapper.toResponseDTO(savedDoc);
    }
}
