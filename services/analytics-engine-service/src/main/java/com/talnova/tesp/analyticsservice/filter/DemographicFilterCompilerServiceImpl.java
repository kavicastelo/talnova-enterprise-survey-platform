package com.talnova.tesp.analyticsservice.filter;

import com.talnova.tesp.analyticsservice.exception.ExcessiveFilterParametersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DemographicFilterCompilerServiceImpl implements DemographicFilterCompilerService {

    private static final Logger log = LoggerFactory.getLogger(DemographicFilterCompilerServiceImpl.class);
    private static final int MAX_DEMOGRAPHIC_FILTERS = 5;
    private static final Set<String> RESERVED_PARAMS = Set.of(
            "campaignid", "projectid", "nodeid", "parentnodeid", "minresponsethreshold", "page", "size", "sort"
    );

    @Override
    public Map<String, String> compileFilters(Map<String, String> rawQueryParams) {
        Map<String, String> compiled = new HashMap<>();

        if (rawQueryParams == null || rawQueryParams.isEmpty()) {
            return compiled;
        }

        for (Map.Entry<String, String> entry : rawQueryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key == null || value == null || value.isBlank()) {
                continue;
            }

            if (RESERVED_PARAMS.contains(key.toLowerCase())) {
                continue;
            }

            compiled.put(key, value);
        }

        if (compiled.size() > MAX_DEMOGRAPHIC_FILTERS) {
            log.warn("Excessive demographic filters provided: {} (max allowed: {})", compiled.size(), MAX_DEMOGRAPHIC_FILTERS);
            throw new ExcessiveFilterParametersException("Excessive filter parameters. Maximum 5 demographic filters allowed per VR-ANL-004");
        }

        return compiled;
    }
}
