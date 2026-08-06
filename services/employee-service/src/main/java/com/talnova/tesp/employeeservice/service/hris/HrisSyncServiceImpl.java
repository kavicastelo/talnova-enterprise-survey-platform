package com.talnova.tesp.employeeservice.service.hris;

import com.talnova.tesp.employeeservice.dto.BulkImportResultDTO;
import com.talnova.tesp.employeeservice.dto.CreateEmployeeDTO;
import com.talnova.tesp.employeeservice.dto.HrisSyncRequestDTO;
import com.talnova.tesp.employeeservice.service.BulkImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class HrisSyncServiceImpl implements HrisSyncService {

    private static final Logger log = LoggerFactory.getLogger(HrisSyncServiceImpl.class);

    private final Map<HrisProvider, HrisSyncAdapter> adapterMap;
    private final BulkImportService bulkImportService;

    public HrisSyncServiceImpl(List<HrisSyncAdapter> adapters, BulkImportService bulkImportService) {
        this.adapterMap = new java.util.EnumMap<>(HrisProvider.class);
        for (HrisSyncAdapter adapter : adapters) {
            this.adapterMap.put(adapter.getProvider(), adapter);
        }
        this.bulkImportService = bulkImportService;
    }

    @Override
    public BulkImportResultDTO syncHrisRoster(HrisSyncRequestDTO request) {
        HrisSyncAdapter adapter = adapterMap.get(request.getProvider());
        if (adapter == null) {
            throw new IllegalArgumentException("Unsupported HRIS provider: " + request.getProvider());
        }

        log.info("Starting HRIS roster sync job for provider {} and project {}", request.getProvider(), request.getProjectId());

        List<CreateEmployeeDTO> roster = adapter.fetchRoster(request.getProjectId(), request.getEndpointUrl(), request.getApiToken());

        // Convert extracted DTOs into CSV stream format for high-throughput batch processing
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("employeeId,fullName,email,phoneNumber,nodeId,matrixNodeIds,status,TenureYears,Location\n");

        for (CreateEmployeeDTO emp : roster) {
            String matrixStr = emp.getMatrixNodeIds() != null ? String.join(";", emp.getMatrixNodeIds()) : "";
            Object tenure = emp.getAttributes() != null ? emp.getAttributes().get("TenureYears") : "";
            Object location = emp.getAttributes() != null ? emp.getAttributes().get("Location") : "";

            csvBuilder.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    emp.getEmployeeId(),
                    emp.getFullName(),
                    emp.getEmail(),
                    emp.getPhoneNumber(),
                    emp.getNodeId(),
                    matrixStr,
                    emp.getStatus().name(),
                    tenure != null ? tenure : "",
                    location != null ? location : ""
            ));
        }

        InputStream csvStream = new ByteArrayInputStream(csvBuilder.toString().getBytes(StandardCharsets.UTF_8));
        return bulkImportService.processCsvImport(request.getProjectId(), csvStream, request.isAutoTerminateMissing());
    }
}
