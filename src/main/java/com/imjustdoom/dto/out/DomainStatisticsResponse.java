package com.imjustdoom.dto.out;

public record DomainStatisticsResponse(String domain, long totalUrls, long totalFailedRequests, String indexedTo, String indexedFrom) {

}