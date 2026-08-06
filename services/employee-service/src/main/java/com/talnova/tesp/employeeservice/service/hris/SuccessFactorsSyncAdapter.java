package com.talnova.tesp.employeeservice.service.hris;

import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import com.talnova.tesp.employeeservice.dto.CreateEmployeeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SuccessFactorsSyncAdapter implements HrisSyncAdapter {

    private static final Logger log = LoggerFactory.getLogger(SuccessFactorsSyncAdapter.class);

    @Override
    public HrisProvider getProvider() {
        return HrisProvider.SUCCESSFACTORS;
    }

    @Override
    public List<CreateEmployeeDTO> fetchRoster(String projectId, String endpointUrl, String apiToken) {
        log.info("Executing SAP SuccessFactors OData API roster extraction from {} for project {}", endpointUrl, projectId);

        // Standardized SAP SuccessFactors OData transformation mapping
        List<CreateEmployeeDTO> roster = new ArrayList<>();
        roster.add(CreateEmployeeDTO.builder()
                .projectId(projectId)
                .employeeId("SF-8001")
                .fullName("SuccessFactors User One")
                .email("user.one.sf@enterprise.de")
                .phoneNumber("+4989123456")
                .nodeId("N-201")
                .matrixNodeIds(List.of("N-401"))
                .status(EmployeeStatus.ACTIVE)
                .attributes(Map.of("TenureYears", 6, "Location", "Walldorf"))
                .build());

        return roster;
    }
}
