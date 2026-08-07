package com.talnova.tesp.employeeservice.service;

import com.mongodb.client.result.UpdateResult;
import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import com.talnova.tesp.employeeservice.dto.BulkImportResultDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class BulkImportServiceImpl implements BulkImportService {

    private static final Logger log = LoggerFactory.getLogger(BulkImportServiceImpl.class);
    private static final int BATCH_SIZE = 5000;

    private final MongoOperations mongoOperations;

    @Value("${app.kafka.topics.emp-events:tesp.emp.events.v1}")
    private String empEventsTopic = "tesp.emp.events.v1";

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public BulkImportServiceImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public BulkImportResultDTO processCsvImport(String projectId, InputStream csvInputStream, boolean autoTerminateMissing) {
        String jobId = "JOB-" + UUID.randomUUID().toString().substring(0, 8);
        int totalProcessed = 0;
        int insertedCount = 0;
        int updatedCount = 0;
        int terminatedCount = 0;
        int failedCount = 0;
        List<String> errors = new ArrayList<>();
        Set<String> importedEmployeeIds = new HashSet<>();

        try (CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build()
                .parse(new InputStreamReader(csvInputStream, StandardCharsets.UTF_8))) {

            Map<String, Integer> headerMap = parser.getHeaderMap();
            log.info("Starting CSV bulk import job {} for project {} with columns {}", jobId, projectId, headerMap.keySet());

            List<CSVRecord> batchRecords = new ArrayList<>();

            for (CSVRecord record : parser) {
                totalProcessed++;
                batchRecords.add(record);

                if (batchRecords.size() >= BATCH_SIZE) {
                    BatchResult batchRes = processBatch(projectId, batchRecords, importedEmployeeIds);
                    insertedCount += batchRes.inserted;
                    updatedCount += batchRes.updated;
                    failedCount += batchRes.failed;
                    errors.addAll(batchRes.errors);
                    batchRecords.clear();
                }
            }

            if (!batchRecords.isEmpty()) {
                BatchResult batchRes = processBatch(projectId, batchRecords, importedEmployeeIds);
                insertedCount += batchRes.inserted;
                updatedCount += batchRes.updated;
                failedCount += batchRes.failed;
                errors.addAll(batchRes.errors);
                batchRecords.clear();
            }

            // Delta Matching & Automatic Termination Engine
            if (autoTerminateMissing && !importedEmployeeIds.isEmpty()) {
                Query terminateQuery = new Query();
                terminateQuery.addCriteria(Criteria.where("projectId").is(projectId)
                        .and("employeeId").nin(importedEmployeeIds)
                        .and("status").is(EmployeeStatus.ACTIVE.name())
                        .and("isDeleted").is(false));

                Update terminateUpdate = new Update()
                        .set("status", EmployeeStatus.TERMINATED.name())
                        .set("updatedAt", Date.from(Instant.now()));

                UpdateResult updateResult = mongoOperations.updateMulti(terminateQuery, terminateUpdate, EmployeeDocument.class);
                terminatedCount = (int) updateResult.getModifiedCount();

                log.info("Delta termination complete for project {}: marked {} unlisted active employees as TERMINATED (job {})",
                        projectId, terminatedCount, jobId);
            }

        } catch (Exception ex) {
            log.error("Fatal error processing CSV bulk import job {}: {}", jobId, ex.getMessage(), ex);
            errors.add("Fatal import error: " + ex.getMessage());
        }

        log.info("CSV bulk import job {} finished for project {}: processed={}, inserted={}, updated={}, terminated={}, failed={}",
                jobId, projectId, totalProcessed, insertedCount, updatedCount, terminatedCount, failedCount);

        if (kafkaTemplate != null && objectMapper != null) {
            try {
                Map<String, Object> eventMap = Map.of(
                        "eventId", "EVT-" + UUID.randomUUID().toString().substring(0, 8),
                        "eventType", "EMPLOYEE_BULK_IMPORT_COMPLETED",
                        "projectId", projectId,
                        "jobId", jobId,
                        "totalRecords", totalProcessed,
                        "inserted", insertedCount,
                        "updated", updatedCount,
                        "terminated", terminatedCount,
                        "timestamp", Instant.now().toString()
                );
                String payload = objectMapper.writeValueAsString(eventMap);
                kafkaTemplate.send(empEventsTopic, projectId, payload);
                log.info("Successfully published EmployeeBulkImportCompletedEvent for job {} to topic {}", jobId, empEventsTopic);
            } catch (Exception ex) {
                log.warn("Failed to publish EmployeeBulkImportCompletedEvent: {}", ex.getMessage());
            }
        }

        return BulkImportResultDTO.builder()
                .jobId(jobId)
                .projectId(projectId)
                .totalProcessed(totalProcessed)
                .insertedCount(insertedCount)
                .updatedCount(updatedCount)
                .terminatedCount(terminatedCount)
                .failedCount(failedCount)
                .errors(errors)
                .build();
    }

    private BatchResult processBatch(String projectId, List<CSVRecord> records, Set<String> importedEmployeeIds) {
        int inserted = 0;
        int updated = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        BulkOperations bulkOps = mongoOperations.bulkOps(BulkOperations.BulkMode.UNORDERED, EmployeeDocument.class);
        boolean hasOps = false;

        for (CSVRecord record : records) {
            String employeeId = getRecordValue(record, "employeeId", "employee_id", "emp_id");
            String fullName = getRecordValue(record, "fullName", "full_name", "name");
            String email = getRecordValue(record, "email", "email_address");
            String phoneNumber = getRecordValue(record, "phoneNumber", "phone_number", "phone");
            String nodeId = getRecordValue(record, "nodeId", "node_id", "department_id");
            String matrixNodesStr = getRecordValue(record, "matrixNodeIds", "matrix_nodes");
            String statusStr = getRecordValue(record, "status", "employment_status");

            if (employeeId == null || employeeId.trim().isEmpty() || nodeId == null || nodeId.trim().isEmpty()) {
                failed++;
                errors.add("Row " + record.getRecordNumber() + ": missing required employeeId or nodeId");
                continue;
            }

            importedEmployeeIds.add(employeeId.trim());

            List<String> matrixNodeIds = matrixNodesStr != null && !matrixNodesStr.trim().isEmpty()
                    ? Arrays.stream(matrixNodesStr.split(";")).map(String::trim).toList()
                    : List.of();

            EmployeeStatus status = EmployeeStatus.ACTIVE;
            if (statusStr != null && !statusStr.trim().isEmpty()) {
                try {
                    status = EmployeeStatus.valueOf(statusStr.trim().toUpperCase());
                } catch (Exception ignored) {
                }
            }

            // Extract dynamic demographic attributes
            Map<String, Object> attributes = new HashMap<>();
            for (Map.Entry<String, String> entry : record.toMap().entrySet()) {
                String colName = entry.getKey();
                if (!isCoreHeader(colName) && entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                    attributes.put(colName, entry.getValue().trim());
                }
            }

            Query query = new Query();
            query.addCriteria(Criteria.where("projectId").is(projectId)
                    .and("employeeId").is(employeeId.trim())
                    .and("isDeleted").is(false));

            Update update = new Update()
                    .set("email", email != null ? email.trim() : "")
                    .set("fullName", fullName != null ? fullName.trim() : "")
                    .set("phoneNumber", phoneNumber != null ? phoneNumber.trim() : "")
                    .set("nodeId", nodeId.trim())
                    .set("matrixNodeIds", matrixNodeIds)
                    .set("status", status.name())
                    .set("attributes", attributes)
                    .set("updatedAt", Date.from(Instant.now()))
                    .setOnInsert("projectId", projectId)
                    .setOnInsert("employeeId", employeeId.trim())
                    .setOnInsert("isDeleted", false)
                    .setOnInsert("createdAt", Date.from(Instant.now()));

            bulkOps.upsert(query, update);
            hasOps = true;
            updated++;
        }

        if (hasOps) {
            try {
                bulkOps.execute();
            } catch (Exception ex) {
                log.error("Error executing MongoDB bulk operations batch: {}", ex.getMessage(), ex);
                errors.add("Bulk database execution error: " + ex.getMessage());
            }
        }

        return new BatchResult(inserted, updated, failed, errors);
    }

    private String getRecordValue(CSVRecord record, String... headerAliases) {
        for (String alias : headerAliases) {
            if (record.isMapped(alias)) {
                String val = record.get(alias);
                if (val != null && !val.trim().isEmpty()) {
                    return val.trim();
                }
            }
        }
        return null;
    }

    private boolean isCoreHeader(String header) {
        if (header == null) return true;
        String h = header.toLowerCase().replaceAll("[_-]", "");
        return h.equals("employeeid") || h.equals("empid") ||
                h.equals("fullname") || h.equals("name") ||
                h.equals("email") || h.equals("emailaddress") ||
                h.equals("phonenumber") || h.equals("phone") ||
                h.equals("nodeid") || h.equals("departmentid") ||
                h.equals("matrixnodeids") || h.equals("matrixnodes") ||
                h.equals("status") || h.equals("employmentstatus");
    }

    private record BatchResult(int inserted, int updated, int failed, List<String> errors) {}
}
