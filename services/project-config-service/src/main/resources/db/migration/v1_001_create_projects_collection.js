// MongoDB Collection Creation and $jsonSchema Validation Script for 'projects'
db.createCollection("projects", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "projectId",
        "name",
        "status",
        "branding",
        "supportedLocales",
        "defaultLocale",
        "features",
        "version",
        "isDeleted"
      ],
      properties: {
        _id: {
          bsonType: "objectId"
        },
        projectId: {
          bsonType: "string",
          pattern: "^PRJ-[A-Z0-9]{4,10}$",
          description: "Immutable unique project identifier matching ^PRJ-[A-Z0-9]{4,10}$"
        },
        name: {
          bsonType: "string",
          description: "Human readable project display name"
        },
        status: {
          enum: ["DRAFT", "ACTIVE", "SUSPENDED", "ARCHIVED"],
          description: "Lifecycle state of the project workspace"
        },
        branding: {
          bsonType: "object",
          required: ["companyName", "primaryColor", "secondaryColor"],
          properties: {
            companyName: {
              bsonType: "string"
            },
            logoUrl: {
              bsonType: "string"
            },
            primaryColor: {
              bsonType: "string",
              pattern: "^#([A-Fa-f0-9]{6})$",
              description: "Must be a valid 6-character hex color"
            },
            secondaryColor: {
              bsonType: "string",
              pattern: "^#([A-Fa-f0-9]{6})$",
              description: "Must be a valid 6-character hex color"
            },
            customCssUrl: {
              bsonType: "string"
            }
          }
        },
        supportedLocales: {
          bsonType: "array",
          minItems: 1,
          items: {
            bsonType: "string"
          },
          description: "List of supported BCP-47 locale tags"
        },
        defaultLocale: {
          bsonType: "string",
          description: "Fallback locale, must be present in supportedLocales"
        },
        features: {
          bsonType: "object",
          properties: {
            aiAnalyticsEnabled: { bsonType: "bool" },
            actionPlanningEnabled: { bsonType: "bool" },
            kioskModeEnabled: { bsonType: "bool" },
            smsDistributionEnabled: { bsonType: "bool" }
          }
        },
        customAttributeDefinitions: {
          bsonType: "array",
          items: {
            bsonType: "object",
            required: ["key", "displayName", "dataType"],
            properties: {
              key: { bsonType: "string" },
              displayName: { bsonType: "string" },
              dataType: { enum: ["STRING", "NUMERIC", "ENUM", "DATE"] },
              allowedValues: {
                bsonType: "array",
                items: { bsonType: "string" }
              }
            }
          }
        },
        version: {
          bsonType: "int",
          minimum: 1,
          description: "Optimistic locking and audit version counter"
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
