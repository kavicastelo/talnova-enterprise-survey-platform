package com.talnova.tesp.employeeservice.service.ai;

import com.talnova.tesp.employeeservice.dto.HeaderMappingResponseDTO;

import java.util.List;

public interface AiHeaderMappingService {

    HeaderMappingResponseDTO mapHeaders(List<String> rawHeaders);
}
