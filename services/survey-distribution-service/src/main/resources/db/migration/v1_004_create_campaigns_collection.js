// MongoDB Migration Script: v1_004_create_campaigns_collection.js
// Target Collection: survey_campaigns in tesp_dist_db

db.createCollection("survey_campaigns", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "projectId",
        "campaignId",
        "surveyId",
        "surveyVersion",
        "title",
        "anonymityLevel",
        "channels",
        "status",
        "startDate",
        "expirationDate"
      ],
      properties: {
        _id: { bsonType: "objectId" },
        projectId: { bsonType: "string" },
        campaignId: { bsonType: "string" },
        surveyId: { bsonType: "string" },
        surveyVersion: { bsonType: "int" },
        title: { bsonType: "string" },
        anonymityLevel: {
          enum: ["AUTHENTICATED", "SEMI_ANONYMOUS", "FULLY_ANONYMOUS", "KIOSK"]
        },
        channels: {
          bsonType: "array",
          items: {
            enum: ["EMAIL", "SMS", "QR_CODE", "KIOSK_PIN", "TEAMS", "SLACK"]
          }
        },
        targetAudience: {
          bsonType: "object",
          properties: {
            nodeIds: { bsonType: "array", items: { bsonType: "string" } },
            demographicFilters: { bsonType: "object" }
          }
        },
        schedule: {
          bsonType: "object",
          properties: {
            reminderDates: { bsonType: "array", items: { bsonType: "date" } },
            reminderFrequencyDays: { bsonType: "int" }
          }
        },
        metrics: {
          bsonType: "object",
          properties: {
            totalTargeted: { bsonType: "int" },
            sent: { bsonType: "int" },
            delivered: { bsonType: "int" },
            opened: { bsonType: "int" },
            started: { bsonType: "int" },
            completed: { bsonType: "int" },
            bounced: { bsonType: "int" }
          }
        },
        status: {
          enum: ["DRAFT", "SCHEDULED", "ACTIVE", "PAUSED", "COMPLETED", "EXPIRED", "CANCELLED"]
        },
        startDate: { bsonType: "date" },
        expirationDate: { bsonType: "date" },
        isDeleted: { bsonType: "bool" },
        createdAt: { bsonType: "date" },
        updatedAt: { bsonType: "date" }
      }
    }
  }
});

// Indexes for high-performance campaign querying
db.survey_campaigns.createIndex({ projectId: 1, campaignId: 1 }, { unique: true });
db.survey_campaigns.createIndex({ projectId: 1, status: 1 });
