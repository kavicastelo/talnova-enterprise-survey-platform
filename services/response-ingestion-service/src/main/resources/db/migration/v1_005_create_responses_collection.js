/**
 * MongoDB Migration v1_005: Create survey_responses Write-Once Collection Schema & Compound Indexes
 * Target Database: tesp_response_db
 * Collection: survey_responses
 */

db = db.getSiblingDB('tesp_response_db');

db.createCollection('survey_responses', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['projectId', 'campaignId', 'surveyId', 'surveyVersion', 'respondentType', 'answers', 'submittedAt'],
      properties: {
        _id: { bsonType: 'objectId' },
        projectId: { bsonType: 'string', description: 'Tenant Project Identifier' },
        campaignId: { bsonType: 'string', description: 'Survey Campaign Identifier' },
        surveyId: { bsonType: 'string', description: 'Survey AST Identifier' },
        surveyVersion: { bsonType: 'int', description: 'Published Survey Version' },
        respondentType: {
          enum: ['AUTHENTICATED', 'SEMI_ANONYMOUS', 'FULLY_ANONYMOUS', 'KIOSK'],
          description: 'Anonymity tier mode'
        },
        responseToken: { bsonType: ['string', 'null'], description: 'Hashed single-use response token' },
        nodeId: { bsonType: ['string', 'null'], description: 'Associated Organization Node ID' },
        demographicSnapshot: {
          bsonType: ['object', 'null'],
          description: 'Immutable key-value demographic tags at submission time e.g. { Tenure: "3-5 Years" }'
        },
        answers: {
          bsonType: 'array',
          items: {
            bsonType: 'object',
            required: ['questionId', 'questionType'],
            properties: {
              questionId: { bsonType: 'string' },
              questionType: { bsonType: 'string' },
              numericValue: { bsonType: ['double', 'int', 'null'] },
              textValue: { bsonType: ['string', 'null'] },
              selectedOptions: { bsonType: ['array', 'null'] }
            }
          }
        },
        submittedAt: { bsonType: 'date', description: 'Submission timestamp' },
        isDeleted: { bsonType: 'bool', description: 'Soft deletion flag' }
      }
    }
  }
});

// Create compound index for multi-tenant campaign aggregation lookups
db.survey_responses.createIndex(
  { projectId: 1, campaignId: 1 },
  { name: 'idx_projectId_campaignId' }
);

// Create compound index for single-use token lookups
db.survey_responses.createIndex(
  { responseToken: 1 },
  { name: 'idx_responseToken' }
);

// Create compound index for org node analytics slicing
db.survey_responses.createIndex(
  { projectId: 1, campaignId: 1, nodeId: 1 },
  { name: 'idx_projectId_campaignId_nodeId' }
);

print("Successfully initialized tesp_response_db.survey_responses collection with write-once JSON schema validation & compound indexes.");
