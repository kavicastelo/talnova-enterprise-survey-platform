package com.talnova.tesp.employeeservice.service;

import com.talnova.tesp.employeeservice.dto.BulkImportResultDTO;

import java.io.InputStream;

public interface BulkImportService {

    BulkImportResultDTO processCsvImport(String projectId, InputStream csvInputStream, boolean autoTerminateMissing);
}
