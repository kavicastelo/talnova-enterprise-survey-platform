/**
 * MongoDB Atlas Schema Migration Script: v1_003_create_employees_csfle.js
 * Feature: FEAT-003 Employee Roster & Demographic Attribute Management
 * Target Collection: employees
 * Description: Defines $jsonSchema validation rules and Client-Side Field Level Encryption (CSFLE)
 *              specifications for sensitive PII attributes (email, fullName, phoneNumber).
 */

db.createCollection("employees", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["projectId", "employeeId", "nodeId", "status", "isDeleted"],
      properties: {
        projectId: {
          bsonType: "string",
          description: "Project workspace isolation identifier - required string"
        },
        employeeId: {
          bsonType: "string",
          pattern: "^[A-Za-z0-9_-]{2,30}$",
          description: "Unique employee identification code - required string matching ^[A-Za-z0-9_-]{2,30}$"
        },
        email: {
          encrypt: {
            bsonType: "string",
            algorithm: "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic",
            description: "Deterministic CSFLE encrypted PII email address for exact queries"
          }
        },
        fullName: {
          encrypt: {
            bsonType: "string",
            algorithm: "AEAD_AES_256_CBC_HMAC_SHA_512-Random",
            description: "Randomized CSFLE encrypted PII full legal name"
          }
        },
        phoneNumber: {
          encrypt: {
            bsonType: "string",
            algorithm: "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic",
            description: "Deterministic CSFLE encrypted PII phone number"
          }
        },
        nodeId: {
          bsonType: "string",
          description: "Primary organizational node assignment identifier - required string"
        },
        matrixNodeIds: {
          bsonType: "array",
          items: {
            bsonType: "string"
          },
          description: "Optional array of secondary cross-functional matrix organizational node IDs"
        },
        status: {
          enum: ["ACTIVE", "INACTIVE", "TERMINATED"],
          description: "Employee employment lifecycle status - required enum"
        },
        attributes: {
          bsonType: "object",
          description: "Dynamic key-value demographic attribute map validated against project metamodel"
        },
        isDeleted: {
          bsonType: "bool",
          description: "Soft deletion indicator - required boolean"
        }
      }
    }
  },
  validationLevel: "strict",
  validationAction: "error"
});
