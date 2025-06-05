package com.imjustdoom.service;

import com.imjustdoom.model.FailedRequest;
import com.imjustdoom.model.TopDomain;
import com.imjustdoom.repository.FailedRequestRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Service
public class FailedRequestService {
    private final FailedRequestRepository failedRequestRepository;

    public FailedRequestService(FailedRequestRepository failedRequestRepository) {
        this.failedRequestRepository = failedRequestRepository;
    }

    public void addFailedRequest(int statusCode, int page, TopDomain topDomain) {
        FailedRequest failedRequest = new FailedRequest(statusCode, page, topDomain);
        this.failedRequestRepository.save(failedRequest);
    }

    public List<FailedRequest> getFailedForTopDomain(TopDomain topDomain) {
        return this.failedRequestRepository.findAllByTopDomain(topDomain, Pageable.ofSize(10));
    }

    public void saveFailedRequest(FailedRequest failedRequest) {
        this.failedRequestRepository.save(failedRequest);
    }

    public void removeFailedRequest(FailedRequest failedRequest) {
        this.failedRequestRepository.delete(failedRequest);
    }
}
