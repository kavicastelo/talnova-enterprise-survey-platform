package com.talnova.tesp.employeeservice.mapper;

import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.dto.CreateEmployeeDTO;
import com.talnova.tesp.employeeservice.dto.EmployeeResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDocument toDocument(CreateEmployeeDTO dto) {
        if (dto == null) return null;
        return EmployeeDocument.builder()
                .projectId(dto.getProjectId())
                .employeeId(dto.getEmployeeId())
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .nodeId(dto.getNodeId())
                .matrixNodeIds(dto.getMatrixNodeIds())
                .status(dto.getStatus())
                .attributes(dto.getAttributes())
                .isDeleted(false)
                .build();
    }

    public EmployeeResponseDTO toResponseDTO(EmployeeDocument doc) {
        if (doc == null) return null;
        return EmployeeResponseDTO.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .employeeId(doc.getEmployeeId())
                .email(doc.getEmail())
                .fullName(doc.getFullName())
                .phoneNumber(doc.getPhoneNumber())
                .nodeId(doc.getNodeId())
                .matrixNodeIds(doc.getMatrixNodeIds())
                .status(doc.getStatus())
                .attributes(doc.getAttributes())
                .version(doc.getVersion())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
