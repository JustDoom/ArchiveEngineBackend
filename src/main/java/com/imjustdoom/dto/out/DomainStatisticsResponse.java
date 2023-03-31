package com.imjustdoom.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DomainStatisticsResponse {

    private String domain;
    private long totalUrls;
    private long totalFailedRequests;
    private String indexedTo;
    private String indexedFrom;
}
