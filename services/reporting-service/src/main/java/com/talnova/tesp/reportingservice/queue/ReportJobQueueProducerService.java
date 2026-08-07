package com.talnova.tesp.reportingservice.queue;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;

public interface ReportJobQueueProducerService {

    /**
     * Enqueues report job payload to Redis list 'tesp:reports:queue' per FR-RPT-001.
     */
    void enqueueJob(ReportJobDocument job);
}
