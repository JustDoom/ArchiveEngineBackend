package com.imjustdoom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imjustdoom.model.Url;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeilisearchService {
    private final Client client;
    private final Index index;

    public MeilisearchService(Client client) throws Exception {
        this.client = client;
        // Initialize or get the 'products' index
        this.index = client.index("urls");
    }

    public void indexProduct(Url url) throws Exception {
        // Convert Product to JSON string
        String json = String.format(
                "[{\"id\":\"%s\",\"url\":\"%s\"}]",
                url.getId(), url.getUrl()
        );
        // Add document to index
        index.addDocuments(json);
    }

    public void indexProducts(List<Url> urls) throws Exception {
        // Convert list of products to JSON
        String json = new ObjectMapper().writeValueAsString(urls);
        index.addDocuments(json);
    }
}