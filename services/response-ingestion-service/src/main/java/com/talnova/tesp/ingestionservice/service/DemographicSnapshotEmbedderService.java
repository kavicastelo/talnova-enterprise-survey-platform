package com.talnova.tesp.ingestionservice.service;

import com.talnova.tesp.ingestionservice.domain.model.RespondentType;

import java.util.Map;

public interface DemographicSnapshotEmbedderService {

    /**
     * Extracts non-PII demographic tags from cached token metadata and embeds them into demographicSnapshot.
     * Enforces BR-INT-002 by stripping employeeId, fullName, and email for semi/fully anonymous modes.
     *
     * @param tokenMetadataJson JSON metadata string returned from token burn
     * @param respondentType Anonymity mode tier
     * @return Map containing sanitized demographic key-value tags.
     */
    Map<String, String> embedDemographicSnapshot(String tokenMetadataJson, RespondentType respondentType);
}
