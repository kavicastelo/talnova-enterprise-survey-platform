package com.talnova.tesp.orgservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topics.org-events:tesp.org.events.v1}")
    private String orgEventsTopic;

    @Bean
    public NewTopic orgEventsTopic() {
        return TopicBuilder.name(orgEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
