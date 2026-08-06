package com.talnova.tesp.distservice.domain.model;

/**
 * Enums representing the lifecycle state machine of a survey campaign.
 */
public enum CampaignStatus {
    DRAFT,
    SCHEDULED,
    ACTIVE,
    PAUSED,
    COMPLETED,
    EXPIRED,
    CANCELLED
}
