package com.talnova.tesp.reportingservice.event;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;

public interface ReportEventPublisherService {

    /**
     * Publishes ReportGeneratedEvent to Kafka topic 'tesp.reports.events.v1' per FR-RPT-007 and FR-RPT-008.
     */
    void publishReportGeneratedEvent(ReportJobDocument job, String recipientEmail);
}
