db.createCollection("action_plans", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "projectId",
        "actionPlanId",
        "nodeId",
        "groupId",
        "title",
        "status",
        "assigneeId",
        "targetCompletionDate"
      ],
      properties: {
        _id: { bsonType: "objectId" },
        projectId: { bsonType: "string" },
        actionPlanId: { bsonType: "string" },
        campaignId: { bsonType: "string" },
        nodeId: { bsonType: "string" },
        groupId: { bsonType: "string" },
        title: { bsonType: "string" },
        description: { bsonType: "string" },
        baselineScore: { bsonType: "double" },
        targetScore: { bsonType: "double" },
        postActionScore: { bsonType: "double" },
        status: {
          enum: [
            "DRAFT",
            "PROPOSED",
            "APPROVED",
            "REJECTED",
            "IN_PROGRESS",
            "COMPLETED",
            "VERIFIED",
            "CANCELLED"
          ]
        },
        assigneeId: { bsonType: "string" },
        createdBy: { bsonType: "string" },
        targetCompletionDate: { bsonType: "date" },
        milestones: {
          bsonType: "array",
          items: {
            bsonType: "object",
            required: ["milestoneId", "title", "completed"],
            properties: {
              milestoneId: { bsonType: "string" },
              title: { bsonType: "string" },
              completed: { bsonType: "bool" },
              dueDate: { bsonType: "date" },
              assigneeId: { bsonType: "string" }
            }
          }
        },
        externalSync: {
          bsonType: "object",
          properties: {
            system: { enum: ["JIRA", "MS_PLANNER"] },
            externalKey: { bsonType: "string" },
            lastSyncedAt: { bsonType: "date" }
          }
        },
        createdAt: { bsonType: "date" },
        updatedAt: { bsonType: "date" }
      }
    }
  }
});

db.action_plans.createIndex({ actionPlanId: 1 }, { name: "idx_action_plan_id", unique: true });
db.action_plans.createIndex({ projectId: 1, nodeId: 1, status: 1 }, { name: "idx_proj_node_status" });
db.action_plans.createIndex({ projectId: 1, campaignId: 1 }, { name: "idx_proj_campaign" });
