package com.talnova.tesp.employeeservice.service.hris;

import com.talnova.tesp.employeeservice.dto.BulkImportResultDTO;
import com.talnova.tesp.employeeservice.dto.HrisSyncRequestDTO;

public interface HrisSyncService {

    BulkImportResultDTO syncHrisRoster(HrisSyncRequestDTO request);
}
