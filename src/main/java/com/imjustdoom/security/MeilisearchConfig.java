package com.imjustdoom.security;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.json.JacksonJsonHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeilisearchConfig {

    @Bean
    public Client meilisearchClient() {
        Config config = new Config(
                "http://localhost:7700",
                "",
                new JacksonJsonHandler()
        );
        return new Client(config);
    }
}