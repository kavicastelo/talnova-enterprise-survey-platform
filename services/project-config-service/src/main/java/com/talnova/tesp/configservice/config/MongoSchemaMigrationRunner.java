package com.talnova.tesp.configservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.stereotype.Component;

@Component
public class MongoSchemaMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoSchemaMigrationRunner.class);

    private final MongoOperations mongoOperations;

    public MongoSchemaMigrationRunner(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Running MongoDB Schema Validation Migration for 'projects' collection...");
        try {
            if (!mongoOperations.collectionExists("projects")) {
                MongoJsonSchema schema = MongoJsonSchema.builder()
                        .required("projectId", "name", "status", "branding", "supportedLocales", "defaultLocale", "features", "version", "isDeleted")
                        .build();

                CollectionOptions options = CollectionOptions.empty()
                        .schema(schema);

                mongoOperations.createCollection("projects", options);
                log.info("Successfully created 'projects' collection with MongoDB $jsonSchema validation.");
            } else {
                log.info("'projects' collection already exists. Schema validation active.");
            }
        } catch (Exception ex) {
            log.warn("MongoSchemaMigrationRunner completed with notice: {}", ex.getMessage());
        }
    }
}
