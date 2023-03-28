package com.imjustdoom;

import com.imjustdoom.service.UrlService;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
@ConfigurationPropertiesScan
@AllArgsConstructor
public class ArchiveEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchiveEngineApplication.class, args);
    }
}