package com.talnova.tesp.employeeservice.service.ai;

import com.talnova.tesp.employeeservice.dto.HeaderMapping;
import com.talnova.tesp.employeeservice.dto.HeaderMappingResponseDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiHeaderMappingServiceImpl implements AiHeaderMappingService {

    private static final Map<String, List<String>> SYNONYM_MAP = new HashMap<>();

    static {
        SYNONYM_MAP.put("employeeId", List.of("emp id", "staff code", "worker number", "employee id", "emp_id", "staff_id", "worker_id"));
        SYNONYM_MAP.put("fullName", List.of("full name", "employee name", "staff name", "name", "worker name"));
        SYNONYM_MAP.put("email", List.of("email address", "work email", "mail", "email", "email_address"));
        SYNONYM_MAP.put("phoneNumber", List.of("phone number", "mobile", "phone", "contact", "mobile number"));
        SYNONYM_MAP.put("nodeId", List.of("org node", "dept code", "department", "division id", "node_id", "department_id", "cost_center"));
        SYNONYM_MAP.put("matrixNodeIds", List.of("matrix node", "matrix nodes", "secondary node", "matrix_nodes"));
        SYNONYM_MAP.put("TenureYears", List.of("tenure (years)", "years of service", "tenure", "experience", "tenure_years"));
        SYNONYM_MAP.put("Gender", List.of("sex", "gender identity", "gender"));
        SYNONYM_MAP.put("JoiningDate", List.of("date of joining", "hire date", "start date", "joining_date"));
    }

    private static final Set<String> CORE_FIELDS = Set.of(
            "employeeId", "fullName", "email", "phoneNumber", "nodeId", "matrixNodeIds", "status"
    );

    @Override
    public HeaderMappingResponseDTO mapHeaders(List<String> rawHeaders) {
        if (rawHeaders == null || rawHeaders.isEmpty()) {
            return new HeaderMappingResponseDTO(List.of());
        }

        List<HeaderMapping> mappings = new ArrayList<>();

        for (String rawHeader : rawHeaders) {
            if (rawHeader == null || rawHeader.trim().isEmpty()) {
                continue;
            }

            String normalized = rawHeader.trim().toLowerCase();
            HeaderMapping bestMatch = findBestMatch(rawHeader, normalized);
            mappings.add(bestMatch);
        }

        return new HeaderMappingResponseDTO(mappings);
    }

    private HeaderMapping findBestMatch(String rawHeader, String normalized) {
        // Direct Synonym Matching
        for (Map.Entry<String, List<String>> entry : SYNONYM_MAP.entrySet()) {
            String targetKey = entry.getKey();
            for (String synonym : entry.getValue()) {
                if (normalized.equals(synonym) || normalized.contains(synonym) || synonym.contains(normalized)) {
                    boolean isCore = CORE_FIELDS.contains(targetKey);
                    double confidence = normalized.equals(synonym) ? 0.98 : 0.90;
                    return HeaderMapping.builder()
                            .sourceHeader(rawHeader)
                            .targetAttributeKey(targetKey)
                            .confidence(confidence)
                            .isCoreField(isCore)
                            .build();
                }
            }
        }

        // Fallback: Clean string as custom demographic attribute key
        String cleanKey = rawHeader.trim().replaceAll("[^a-zA-Z0-9_]", "");
        if (cleanKey.isEmpty()) cleanKey = "CustomAttr";

        return HeaderMapping.builder()
                .sourceHeader(rawHeader)
                .targetAttributeKey(cleanKey)
                .confidence(0.70)
                .isCoreField(false)
                .build();
    }
}
