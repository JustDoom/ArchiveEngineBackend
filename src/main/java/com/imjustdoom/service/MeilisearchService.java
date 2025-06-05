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

    public MeilisearchService(Client client) {
        this.client = client;
        this.index = client.index("urls");
    }

    public void indexProducts(List<Url> urls) throws Exception {
        String json = new ObjectMapper().writeValueAsString(urls);
        this.index.addDocuments(json);
    }
}