db.createCollection("ai_insights", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "projectId",
        "campaignId",
        "responseId",
        "questionId",
        "sanitizedText",
        "sentimentScore",
        "sentimentLabel"
      ],
      properties: {
        _id: { bsonType: "objectId" },
        projectId: { bsonType: "string" },
        campaignId: { bsonType: "string" },
        responseId: { bsonType: "string" },
        questionId: { bsonType: "string" },
        sanitizedText: { bsonType: "string" },
        sentimentScore: { bsonType: "double" },
        sentimentLabel: {
          enum: ["POSITIVE", "NEUTRAL", "NEGATIVE"]
        },
        confidence: { bsonType: "double" },
        themes: {
          bsonType: "array",
          items: { bsonType: "string" }
        },
        riskCategory: { bsonType: "string" },
        riskSeverity: {
          enum: ["LOW", "MEDIUM", "HIGH", "CRITICAL"]
        },
        humanOverride: {
          bsonType: "object",
          required: ["overriddenBy", "originalLabel", "newLabel", "overriddenAt"],
          properties: {
            overriddenBy: { bsonType: "string" },
            originalLabel: { bsonType: "string" },
            newLabel: { bsonType: "string" },
            reason: { bsonType: "string" },
            overriddenAt: { bsonType: "date" }
          }
        },
        createdAt: { bsonType: "date" }
      }
    }
  }
});

db.ai_insights.createIndex({ projectId: 1, campaignId: 1 }, { name: "idx_proj_campaign" });
db.ai_insights.createIndex({ responseId: 1 }, { name: "idx_response_id" });
db.ai_insights.createIndex({ projectId: 1, riskSeverity: 1 }, { name: "idx_proj_risk_severity" });
