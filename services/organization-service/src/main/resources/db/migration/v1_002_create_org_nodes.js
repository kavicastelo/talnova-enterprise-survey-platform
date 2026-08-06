// MongoDB Atlas Schema Validation Migration Script for TESP Organization Hierarchy
// Collection: organization_nodes
// Target Database: tesp_org_db

db.createCollection("organization_nodes", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "projectId",
        "nodeId",
        "name",
        "type",
        "path",
        "depth",
        "status",
        "version",
        "isDeleted"
      ],
      properties: {
        _id: { bsonType: "objectId" },
        projectId: {
          bsonType: "string",
          pattern: "^PRJ-[A-Z0-9]{4,10}$",
          description: "Mandatory project workspace identifier"
        },
        nodeId: {
          bsonType: "string",
          pattern: "^N-[A-Za-z0-9_-]{3,20}$",
          description: "Mandatory unique node identifier"
        },
        name: {
          bsonType: "string",
          description: "Display name of the organizational node"
        },
        type: {
          bsonType: "string",
          description: "Configurable Node Type e.g. COMPANY, SECTOR, DIVISION, BRANCH, DEPARTMENT, TEAM"
        },
        parentId: {
          bsonType: ["string", "null"],
          description: "Reference to parent nodeId"
        },
        path: {
          bsonType: "string",
          pattern: "^,([A-Za-z0-9_-]+,)+$",
          description: "Materialized path string e.g. ,N-001,N-101,N-201,"
        },
        depth: {
          bsonType: "int",
          minimum: 1,
          description: "Tree depth level (Root is 1)"
        },
        displayOrder: {
          bsonType: "int",
          description: "Sorting display order index"
        },
        status: {
          enum: ["ACTIVE", "INACTIVE", "ARCHIVED"],
          description: "Node lifecycle status"
        },
        attributes: {
          bsonType: "object",
          description: "Dynamic custom demographic metadata attributes"
        },
        version: {
          bsonType: "int",
          description: "Optimistic locking concurrency counter"
        },
        isDeleted: {
          bsonType: "bool",
          description: "Soft delete flag"
        },
        createdAt: {
          bsonType: "date"
        },
        updatedAt: {
          bsonType: "date"
        }
      }
    }
  }
});
