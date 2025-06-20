package com.imjustdoom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.model.Faceting;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeilisearchService {
    private final Client client;
    private final Index index;

    public MeilisearchService(Client client) {
        this.client = client;
        client.createIndex("urls", "hash");
        this.index = client.getIndex("urls");
        this.index.updateDisplayedAttributesSettings(new String[]{"url", "domain"});
        Faceting faceting = new Faceting();
        faceting.setSortFacetValuesBy(null);
        faceting.setMaxValuesPerFacet(0);
        this.index.updateFacetingSettings(faceting);
        this.index.updateRankingRulesSettings(new String[]{
                "words",
                "typo",
                "attribute",
                "sort",
                "exactness"
        });
        this.index.updateProximityPrecisionSettings("byAttribute");
        this.index.updateSearchableAttributesSettings(new String[]{"url"});
        this.index.updateFilterableAttributesSettings(new String[]{"domain"});
    }

    public void indexProducts(List<IndexUrl> urls) throws Exception {
        String json = new ObjectMapper().writeValueAsString(urls);
        this.index.addDocuments(json);
    }

    public record IndexUrl(String url, String hash, String domain) {}
}