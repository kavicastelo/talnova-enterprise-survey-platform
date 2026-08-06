package com.talnova.tesp.surveyservice.domain.model;

/**
 * Enums representing the dispatch status of an outbox event.
 */
public enum OutboxStatus {
    PENDING,
    PROCESSED,
    FAILED
}
