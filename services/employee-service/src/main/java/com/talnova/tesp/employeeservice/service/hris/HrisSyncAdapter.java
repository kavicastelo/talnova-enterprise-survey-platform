package com.talnova.tesp.employeeservice.service.hris;

import com.talnova.tesp.employeeservice.dto.CreateEmployeeDTO;

import java.util.List;

public interface HrisSyncAdapter {

    HrisProvider getProvider();

    List<CreateEmployeeDTO> fetchRoster(String projectId, String endpointUrl, String apiToken);
}
