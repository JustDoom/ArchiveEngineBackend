package com.imjustdoom.service;

import com.imjustdoom.dto.out.DomainStatisticsResponse;
import com.imjustdoom.dto.out.SimpleUrlResponse;
import com.imjustdoom.dto.out.StatisticsResponse;
import com.imjustdoom.model.Url;
import com.imjustdoom.repository.DomainRepository;
import com.imjustdoom.repository.FailedRequestRepository;
import com.imjustdoom.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final FailedRequestRepository failedRequestRepository;
    private final DomainRepository domainRepository;

    public ResponseEntity<?> searchChecks(String query, String page, String sortBy, String ascending) {
        if (query == null || query.isEmpty()) return ResponseEntity.badRequest().body("Query cannot be empty");
        if (!page.matches("[0-9]+")) return ResponseEntity.badRequest().body("Page must be a number");
        if (!sortBy.matches("(?i)timestamp|url|mimetype|statuscode")) return ResponseEntity.badRequest().body("sortBy must be either timestamp, mimeType, statusCode or url");
        if (!ascending.matches("true|false")) return ResponseEntity.badRequest().body("ascending must be either true or false");

        return ResponseEntity.ok().body(search(query, page, sortBy, ascending));
    }

    public List<SimpleUrlResponse> search(String query, String page, String sortBy, String ascending) {
        return this.urlRepository.findAllByUrlIsContainingIgnoreCase(query, PageRequest.of(Integer.parseInt(page), 50, Boolean.parseBoolean(ascending) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()))
                .stream().map(url -> new SimpleUrlResponse(url.getUrl(), url.getTimestamp(), url.getMimeType(), url.getStatusCode())).toList();
    }

    public void addAllUrl(List<Url> urls) {
        this.urlRepository.saveAll(urls);
    }

    public ResponseEntity<?> getStatistics() {
        return ResponseEntity.ok().body(new StatisticsResponse(this.domainRepository.findAll()
                .stream().map(domain -> new DomainStatisticsResponse(
                        domain.getDomain(),
                        this.urlRepository.countAllByDomain(domain),
                        this.failedRequestRepository.countAllByDomain(domain),
                        domain.getTimestamp(),
                        this.urlRepository.findFirstByDomainOrderByTimestampAsc(domain).map(Url::getTimestamp).orElse("N/A")))
                .collect(Collectors.toList())));
    }
}
