// v1_006_create_analytics_snapshots.js
// MongoDB collection migration script for analytical_snapshots collection

db.createCollection("analytical_snapshots", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "projectId",
        "campaignId",
        "surveyId",
        "snapshotDate",
        "totalResponses",
        "overallEnps",
        "overallEngagementIndex",
        "nodeAggregates"
      ],
      properties: {
        _id: { bsonType: "objectId" },
        projectId: { bsonType: "string", description: "Tenant project identifier" },
        campaignId: { bsonType: "string", description: "Survey campaign identifier" },
        surveyId: { bsonType: "string", description: "Survey identifier" },
        snapshotDate: { bsonType: "date", description: "Point-in-time snapshot timestamp" },
        totalResponses: { bsonType: "int", description: "Total aggregated responses" },
        overallEnps: { bsonType: "double", description: "Overall eNPS score (-100 to +100)" },
        overallEngagementIndex: { bsonType: "double", description: "Overall 100-point engagement index" },
        participationRate: { bsonType: "double", description: "Participation rate percentage" },
        groupScores: {
          bsonType: "array",
          items: {
            bsonType: "object",
            required: ["groupId", "score"],
            properties: {
              groupId: { bsonType: "string" },
              score: { bsonType: "double" }
            }
          }
        },
        nodeAggregates: {
          bsonType: "array",
          items: {
            bsonType: "object",
            required: ["nodeId", "nodePath", "responseCount", "status"],
            properties: {
              nodeId: { bsonType: "string" },
              nodePath: { bsonType: "string" },
              responseCount: { bsonType: "int" },
              status: {
                enum: ["VALID", "SUPPRESSED"],
                description: "Privacy status (SUPPRESSED if N < 5)"
              },
              enps: { bsonType: ["double", "null"] },
              engagementIndex: { bsonType: ["double", "null"] }
            }
          }
        },
        createdAt: { bsonType: "date" }
      }
    }
  }
});

// Indexes for fast lookup & longitudinal baseline comparison
db.analytical_snapshots.createIndex(
  { projectId: 1, campaignId: 1 },
  { name: "idx_proj_campaign", unique: false }
);

db.analytical_snapshots.createIndex(
  { projectId: 1, snapshotDate: -1 },
  { name: "idx_proj_snapshot_date", unique: false }
);
