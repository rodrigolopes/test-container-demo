package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestDemoApplication {

    @TestConfiguration
    public static class DemoTestConfiguration {
        @Bean
        @ServiceConnection
        public PostgreSQLContainer<?> postgresContainer( ){
            return new PostgreSQLContainer<>("postgres");
        }
    }

    public static void main(String[] args) {
        SpringApplication
                .from(DemoApplication::main)
                .with(DemoTestConfiguration.class)
                .run(args);
    }
}
