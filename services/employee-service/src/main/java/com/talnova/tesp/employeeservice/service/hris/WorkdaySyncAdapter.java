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
public class WorkdaySyncAdapter implements HrisSyncAdapter {

    private static final Logger log = LoggerFactory.getLogger(WorkdaySyncAdapter.class);

    @Override
    public HrisProvider getProvider() {
        return HrisProvider.WORKDAY;
    }

    @Override
    public List<CreateEmployeeDTO> fetchRoster(String projectId, String endpointUrl, String apiToken) {
        log.info("Executing Workday RaaS API roster extraction from {} for project {}", endpointUrl, projectId);

        // Standardized Workday RaaS transformation mapping mock / integration layer
        List<CreateEmployeeDTO> roster = new ArrayList<>();
        roster.add(CreateEmployeeDTO.builder()
                .projectId(projectId)
                .employeeId("WD-9001")
                .fullName("Workday User Alpha")
                .email("alpha.wd@enterprise.com")
                .phoneNumber("+14155550199")
                .nodeId("N-201")
                .matrixNodeIds(List.of("N-301"))
                .status(EmployeeStatus.ACTIVE)
                .attributes(Map.of("TenureYears", 4, "Location", "San Francisco"))
                .build());

        roster.add(CreateEmployeeDTO.builder()
                .projectId(projectId)
                .employeeId("WD-9002")
                .fullName("Workday User Beta")
                .email("beta.wd@enterprise.com")
                .phoneNumber("+14155550200")
                .nodeId("N-202")
                .matrixNodeIds(List.of())
                .status(EmployeeStatus.ACTIVE)
                .attributes(Map.of("TenureYears", 2, "Location", "New York"))
                .build());

        return roster;
    }
}
