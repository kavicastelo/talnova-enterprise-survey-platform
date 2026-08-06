package com.talnova.tesp.employeeservice.service;

import com.talnova.tesp.employeeservice.domain.DemographicSnapshotDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import com.talnova.tesp.employeeservice.dto.CompileSnapshotRequestDTO;
import com.talnova.tesp.employeeservice.dto.DemographicSnapshotDTO;
import com.talnova.tesp.employeeservice.exception.EmployeeNotFoundException;
import com.talnova.tesp.employeeservice.repository.DemographicSnapshotRepository;
import com.talnova.tesp.employeeservice.repository.EmployeeRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DemographicSnapshotServiceImpl implements DemographicSnapshotService {

    private final EmployeeRepository employeeRepository;
    private final DemographicSnapshotRepository snapshotRepository;
    private final MongoOperations mongoOperations;

    public DemographicSnapshotServiceImpl(EmployeeRepository employeeRepository,
                                           DemographicSnapshotRepository snapshotRepository,
                                           MongoOperations mongoOperations) {
        this.employeeRepository = employeeRepository;
        this.snapshotRepository = snapshotRepository;
        this.mongoOperations = mongoOperations;
    }

    @Override
    @Transactional
    public List<DemographicSnapshotDTO> compileSurveySnapshot(CompileSnapshotRequestDTO request) {
        Query query = new Query();
        Criteria criteria = Criteria.where("projectId").is(request.getProjectId())
                .and("isDeleted").is(false);

        if (request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()) {
            criteria.and("employeeId").in(request.getEmployeeIds());
        } else {
            criteria.and("status").is(EmployeeStatus.ACTIVE.name());
        }

        query.addCriteria(criteria);
        List<EmployeeDocument> employees = mongoOperations.find(query, EmployeeDocument.class);

        Instant now = Instant.now();
        List<DemographicSnapshotDocument> snapshots = employees.stream().map(emp -> {
            Map<String, Object> frozenDemographics = new HashMap<>();
            if (emp.getAttributes() != null) {
                frozenDemographics.putAll(emp.getAttributes());
            }
            frozenDemographics.put("nodeId", emp.getNodeId());
            frozenDemographics.put("matrixNodeIds", emp.getMatrixNodeIds() != null ? emp.getMatrixNodeIds() : List.of());
            frozenDemographics.put("status", emp.getStatus() != null ? emp.getStatus().name() : "ACTIVE");

            return DemographicSnapshotDocument.builder()
                    .projectId(request.getProjectId())
                    .surveyId(request.getSurveyId())
                    .employeeId(emp.getEmployeeId())
                    .nodeId(emp.getNodeId())
                    .matrixNodeIds(emp.getMatrixNodeIds())
                    .demographics(Collections.unmodifiableMap(frozenDemographics))
                    .capturedAt(now)
                    .build();
        }).collect(Collectors.toList());

        List<DemographicSnapshotDocument> savedSnapshots = snapshotRepository.saveAll(snapshots);

        return savedSnapshots.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DemographicSnapshotDTO getSnapshot(String id) {
        DemographicSnapshotDocument doc = snapshotRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Snapshot ID: " + id));
        return toDTO(doc);
    }

    private DemographicSnapshotDTO toDTO(DemographicSnapshotDocument doc) {
        return DemographicSnapshotDTO.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .surveyId(doc.getSurveyId())
                .employeeId(doc.getEmployeeId())
                .nodeId(doc.getNodeId())
                .matrixNodeIds(doc.getMatrixNodeIds())
                .demographics(doc.getDemographics())
                .capturedAt(doc.getCapturedAt())
                .build();
    }
}
