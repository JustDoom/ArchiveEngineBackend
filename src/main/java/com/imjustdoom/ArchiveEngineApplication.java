package com.imjustdoom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
@ConfigurationPropertiesScan
public class ArchiveEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArchiveEngineApplication.class, args);
    }
}