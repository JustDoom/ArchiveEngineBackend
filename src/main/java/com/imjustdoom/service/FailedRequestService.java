package com.imjustdoom.service;

import com.imjustdoom.indexer.Timestamp;
import com.imjustdoom.model.Domain;
import com.imjustdoom.model.FailedRequest;
import com.imjustdoom.repository.FailedRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@RequiredArgsConstructor
public class FailedRequestService {

    private final FailedRequestRepository failedRequestRepository;

    public void addFailedRequest(String timestamp, Timestamp.Time time, Domain domain) {
        if (this.failedRequestRepository.existsByTimestampAndDomain(timestamp, domain)) return;
        FailedRequest failedRequest = new FailedRequest(timestamp, time, domain);
        this.failedRequestRepository.save(failedRequest);
    }
}
