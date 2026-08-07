package com.talnova.tesp.analyticsservice.filter;

import java.util.Map;

public interface DemographicFilterCompilerService {

    /**
     * Parses, sanitizes, and validates demographic filter parameters.
     * Enforces VR-ANL-004 limit of max 5 concurrent demographic filters.
     *
     * @param rawQueryParams Raw query parameter map
     * @return Cleaned demographic filter map.
     */
    Map<String, String> compileFilters(Map<String, String> rawQueryParams);
}
