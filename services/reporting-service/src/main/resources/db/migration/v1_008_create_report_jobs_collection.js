db.createCollection("report_jobs", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "projectId",
        "jobId",
        "reportType",
        "campaignId",
        "requestedBy",
        "status"
      ],
      properties: {
        _id: { bsonType: "objectId" },
        projectId: { bsonType: "string" },
        jobId: { bsonType: "string" },
        reportType: {
          enum: ["EXEC_SUMMARY_PDF", "DEPT_BREAKDOWN_PDF", "RAW_RESPONSES_XLSX", "AGGREGATED_SCORES_XLSX"]
        },
        campaignId: { bsonType: "string" },
        nodeId: { bsonType: "string" },
        requestedBy: { bsonType: "string" },
        status: {
          enum: ["QUEUED", "PROCESSING", "COMPLETED", "FAILED"]
        },
        downloadUrl: { bsonType: "string" },
        expiresAt: { bsonType: "date" },
        errorMessage: { bsonType: "string" },
        createdAt: { bsonType: "date" },
        completedAt: { bsonType: "date" }
      }
    }
  }
});

db.report_jobs.createIndex({ projectId: 1, jobId: 1 }, { name: "idx_proj_job", unique: true });
db.report_jobs.createIndex({ status: 1 }, { name: "idx_status" });
db.report_jobs.createIndex({ projectId: 1, campaignId: 1 }, { name: "idx_proj_campaign" });
