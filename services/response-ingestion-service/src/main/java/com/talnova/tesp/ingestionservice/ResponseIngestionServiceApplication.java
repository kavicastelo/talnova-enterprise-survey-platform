package com.talnova.tesp.ingestionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories(basePackages = "com.talnova.tesp.ingestionservice.repository")
public class ResponseIngestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResponseIngestionServiceApplication.class, args);
    }
}
