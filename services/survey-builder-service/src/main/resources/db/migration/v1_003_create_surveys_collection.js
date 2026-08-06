// MongoDB Collection Creation and $jsonSchema Validation Script for 'surveys' AST Collection
db.createCollection("surveys", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: [
        "projectId",
        "surveyId",
        "version",
        "title",
        "status",
        "pages",
        "isDeleted",
        "createdAt",
        "updatedAt"
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
        surveyId: {
          bsonType: "string",
          pattern: "^SRV-[A-Za-z0-9_-]{3,20}$",
          description: "Immutable survey identifier matching ^SRV-[A-Za-z0-9_-]{3,20}$"
        },
        version: {
          bsonType: "int",
          minimum: 1,
          description: "Monotonically increasing version number for immutable version publishing"
        },
        title: {
          bsonType: "object",
          description: "Localized key-value map for survey title, e.g. { 'en-US': 'Annual Engagement Survey' }"
        },
        description: {
          bsonType: "object",
          description: "Localized key-value map for survey description"
        },
        status: {
          enum: ["DRAFT", "PUBLISHED", "ACTIVE", "CLOSED", "ARCHIVED"],
          description: "Lifecycle status of the survey AST version"
        },
        pages: {
          bsonType: "array",
          description: "Hierarchical list of survey pages",
          items: {
            bsonType: "object",
            required: ["pageId", "pageOrder", "sections"],
            properties: {
              pageId: {
                bsonType: "string",
                description: "Unique page identifier within survey AST"
              },
              pageOrder: {
                bsonType: "int",
                minimum: 1,
                description: "1-based order index of the page"
              },
              title: {
                bsonType: "object",
                description: "Localized title map for page"
              },
              sections: {
                bsonType: "array",
                description: "List of sections within a page",
                items: {
                  bsonType: "object",
                  required: ["sectionId", "questions"],
                  properties: {
                    sectionId: {
                      bsonType: "string",
                      description: "Unique section identifier within survey AST"
                    },
                    title: {
                      bsonType: "object",
                      description: "Localized title map for section"
                    },
                    questions: {
                      bsonType: "array",
                      description: "List of questions within a section",
                      items: {
                        bsonType: "object",
                        required: ["questionId", "type", "groupId", "prompt"],
                        properties: {
                          questionId: {
                            bsonType: "string",
                            description: "Unique question identifier within survey document"
                          },
                          type: {
                            enum: [
                              "LIKERT",
                              "NPS",
                              "MATRIX",
                              "SINGLE_CHOICE",
                              "MULTIPLE_CHOICE",
                              "RANKING",
                              "SHORT_TEXT",
                              "LONG_TEXT",
                              "NUMERIC",
                              "DATE"
                            ],
                            description: "Supported question type enum"
                          },
                          groupId: {
                            bsonType: "string",
                            description: "Mandatory analytic question group identifier (e.g. GRP-LEADERSHIP)"
                          },
                          prompt: {
                            bsonType: "object",
                            description: "Localized prompt map, must contain default locale entry"
                          },
                          isMandatory: {
                            bsonType: "bool",
                            description: "Flag specifying if answering question is mandatory"
                          },
                          logicRules: {
                            bsonType: "array",
                            description: "Declarative logic rule AST objects",
                            items: {
                              bsonType: "object",
                              required: ["ruleId", "operator", "targetPageId"],
                              properties: {
                                ruleId: { bsonType: "string" },
                                operator: { enum: ["EQUALS", "NOT_EQUALS", "LESS_THAN", "GREATER_THAN"] },
                                comparisonValue: { bsonType: "string" },
                                targetPageId: { bsonType: "string" }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        },
        publishedAt: {
          bsonType: ["date", "null"],
          description: "Timestamp when survey version was published"
        },
        versionHistory: {
          bsonType: "array",
          description: "Audit trail of previous published version numbers and timestamps"
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
