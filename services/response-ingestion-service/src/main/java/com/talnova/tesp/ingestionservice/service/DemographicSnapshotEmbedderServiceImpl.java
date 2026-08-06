package com.talnova.tesp.ingestionservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DemographicSnapshotEmbedderServiceImpl implements DemographicSnapshotEmbedderService {

    private static final Logger log = LoggerFactory.getLogger(DemographicSnapshotEmbedderServiceImpl.class);
    private static final Set<String> PII_KEYS = Set.of("employeeid", "fullname", "name", "email", "phone", "contact");

    private final ObjectMapper objectMapper;

    public DemographicSnapshotEmbedderServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, String> embedDemographicSnapshot(String tokenMetadataJson, RespondentType respondentType) {
        Map<String, String> snapshot = new HashMap<>();

        if (tokenMetadataJson == null || tokenMetadataJson.isBlank()) {
            return snapshot;
        }

        try {
            Map<String, Object> rawMap = objectMapper.readValue(tokenMetadataJson, new TypeReference<Map<String, Object>>() {});
            boolean isAnonymousMode = respondentType == RespondentType.SEMI_ANONYMOUS || respondentType == RespondentType.FULLY_ANONYMOUS;

            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                String key = entry.getKey();
                String lowerKey = key.toLowerCase();

                if (isAnonymousMode && PII_KEYS.contains(lowerKey)) {
                    log.debug("Sanitizing PII key '{}' from response demographic snapshot per BR-INT-002", key);
                    continue;
                }

                if (entry.getValue() != null) {
                    snapshot.put(key, entry.getValue().toString());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse token metadata JSON for demographic snapshot embedding: {}", e.getMessage());
        }

        return snapshot;
    }
}
