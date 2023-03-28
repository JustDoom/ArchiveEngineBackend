package com.imjustdoom.service;

import com.imjustdoom.Timestamp;
import com.imjustdoom.dto.out.SimpleUrlResponse;
import com.imjustdoom.model.Domain;
import com.imjustdoom.model.FailedRequest;
import com.imjustdoom.model.Url;
import com.imjustdoom.repository.DomainRepository;
import com.imjustdoom.repository.FailedRequestRepository;
import com.imjustdoom.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final FailedRequestRepository failedRequestRepository;
    private final DomainRepository domainRepository;

    public List<SimpleUrlResponse> search(String query) {
        return this.urlRepository.findAllByUrlIsContainingIgnoreCase(query).stream().map(url -> new SimpleUrlResponse(url.getUrl())).toList();
    }

    public void addUrl(String url, String mimeType, String timestamp, String endTimestamp, String statusCode, Domain domain) {
        if (this.urlRepository.existsByUrlAndMimeTypeAndTimestampAndEndTimestampAndStatusCode(url, mimeType, timestamp, endTimestamp, statusCode)) return;
        Url url1 = new Url(url, mimeType, timestamp, endTimestamp, statusCode, domain);
        this.urlRepository.save(url1);
    }

    public void addFailedRequest(String timestamp, Timestamp.Time time, Domain domain) {
        if (this.failedRequestRepository.existsByTimestampAndDomain(timestamp, domain)) return;
        FailedRequest failedRequest = new FailedRequest(timestamp, time, domain);
        this.failedRequestRepository.save(failedRequest);
    }

    public boolean domainExists(String domain) {
        return this.domainRepository.existsByDomain(domain);
    }

    public void addDomain(String domain) {
        this.domainRepository.save(new Domain(domain));
    }

    public Optional<Domain> getDomain(String domain) {
        return this.domainRepository.findByDomain(domain);
    }
}
