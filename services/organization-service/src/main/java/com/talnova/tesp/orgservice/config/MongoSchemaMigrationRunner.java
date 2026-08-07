package com.talnova.tesp.orgservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class MongoSchemaMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoSchemaMigrationRunner.class);

    private final MongoOperations mongoOperations;

    public MongoSchemaMigrationRunner(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Running MongoDB Schema Validation Migration for 'organization_nodes' collection...");
        try {
            if (!mongoOperations.collectionExists("organization_nodes")) {
                MongoJsonSchema schema = MongoJsonSchema.builder()
                        .required("projectId", "nodeId", "name", "type", "path", "depth", "status", "version", "isDeleted")
                        .build();

                CollectionOptions options = CollectionOptions.empty()
                        .schema(schema);

                mongoOperations.createCollection("organization_nodes", options);
                log.info("Successfully created 'organization_nodes' collection with MongoDB $jsonSchema validation.");
            } else {
                log.info("'organization_nodes' collection already exists. Schema validation active.");
            }
        } catch (Exception ex) {
            log.warn("MongoSchemaMigrationRunner completed with notice: {}", ex.getMessage());
        }
    }
}
