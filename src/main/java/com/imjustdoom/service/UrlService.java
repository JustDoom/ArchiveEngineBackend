package com.imjustdoom.service;

import com.imjustdoom.model.Url;
import com.imjustdoom.repository.UrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Service
public class UrlService {
    private final UrlRepository urlRepository;
//    private final FailedRequestRepository failedRequestRepository;
//    private final TopDomainRepository topDomainRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
//        this.failedRequestRepository = failedRequestRepository;
//        this.topDomainRepository = topDomainRepository;
    }

//    public ResponseEntity<?> searchChecks(String query, String page, String sortBy, String ascending) {
//        if (query == null || query.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("Query cannot be empty"));
//        if (!page.matches("[0-9]+")) return ResponseEntity.badRequest().body(new MessageResponse("Page must be a number"));
//        if (!sortBy.matches("(?i)timestamp|url|mimetype|statuscode")) return ResponseEntity.badRequest().body(new MessageResponse("sortBy must be either timestamp, mimeType, statusCode or url"));
//        if (!ascending.matches("true|false")) return ResponseEntity.badRequest().body(new MessageResponse("ascending must be either true or false"));
//
//        long start = System.currentTimeMillis();
//        List<SimpleUrlResponse> responses = search(query, page, sortBy, ascending);
//        System.out.println(System.currentTimeMillis() - start);
//        return ResponseEntity.ok().body(responses);
//    }

//    public List<SimpleUrlResponse> search(String query, String page, String sortBy, String ascending) {
//        return this.urlRepository.findAllByUrlIsContainingIgnoreCase(query, PageRequest.of(Integer.parseInt(page), 50, Boolean.parseBoolean(ascending) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()))
//                .stream().map(url -> new SimpleUrlResponse(url.getUrl())).toList();
//    }

    public boolean exists(String url) {
        return this.urlRepository.existsByUrl(url);
    }

    public void addAllUrl(List<Url> urls) {
        this.urlRepository.saveAll(urls);
    }

//    public ResponseEntity<?> getStatistics() {
//        return ResponseEntity.ok().body(new StatisticsResponse(this.topDomainRepository.findAll()
//                .stream().map(domain -> new DomainStatisticsResponse(
//                        domain.getDomain(),
//                        this.urlRepository.countAllByDomain(domain),
//                        this.failedRequestRepository.countAllByDomain(domain),
//                        this.urlRepository.findFirstByDomainOrderByTimestampDesc(domain).map(Url::getTimestamp).orElse("N/A"),
//                        this.urlRepository.findFirstByDomainOrderByTimestampAsc(domain).map(Url::getTimestamp).orElse("N/A")))
//                .collect(Collectors.toList())));
//    }
}
