package com.talnova.tesp.distservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SurveyDistributionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurveyDistributionServiceApplication.class, args);
    }
}
