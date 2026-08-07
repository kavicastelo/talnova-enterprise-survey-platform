package com.talnova.tesp.employeeservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("!test")
public class KafkaConfig {

    @Value("${app.kafka.topics.emp-events:tesp.emp.events.v1}")
    private String empEventsTopic;

    @Bean
    public NewTopic empEventsTopic() {
        return TopicBuilder.name(empEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
