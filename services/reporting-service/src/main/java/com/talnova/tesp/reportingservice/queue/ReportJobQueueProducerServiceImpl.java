package com.talnova.tesp.reportingservice.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportJobQueueProducerServiceImpl implements ReportJobQueueProducerService {

    private static final Logger log = LoggerFactory.getLogger(ReportJobQueueProducerServiceImpl.class);
    private static final String QUEUE_KEY = "tesp:reports:queue";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReportJobQueueProducerServiceImpl(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void enqueueJob(ReportJobDocument job) {
        log.info("Enqueueing report job '{}' (type: {}, project: {}) to Redis queue '{}' per FR-RPT-001",
                job.getJobId(), job.getReportType(), job.getProjectId(), QUEUE_KEY);
        try {
            String jsonPayload = objectMapper.writeValueAsString(job);
            redisTemplate.opsForList().rightPush(QUEUE_KEY, jsonPayload);
            log.info("Successfully enqueued report job '{}' to Redis list '{}'", job.getJobId(), QUEUE_KEY);
        } catch (Exception e) {
            log.error("Failed to enqueue report job '{}' to Redis queue '{}'", job.getJobId(), QUEUE_KEY, e);
            throw new RuntimeException("Redis queue enqueue failed for jobId: " + job.getJobId(), e);
        }
    }
}
