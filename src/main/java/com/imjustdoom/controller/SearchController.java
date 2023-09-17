package com.imjustdoom.controller;

import com.imjustdoom.model.Domain;
import com.imjustdoom.repository.DomainRepository;
import com.imjustdoom.repository.UrlRepository;
import com.imjustdoom.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final UrlService urlService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("query") String query,
                                    @RequestParam(value = "page", required = false, defaultValue = "0") String page,
                                    @RequestParam(value = "sortBy", required = false, defaultValue = "timestamp") String sortBy,
                                    @RequestParam(value = "ascending", required = false, defaultValue = "true") String ascending) {
        return this.urlService.searchChecks(query, page, sortBy, ascending);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> statistics() {
        return this.urlService.getStatistics();
    }

    private final DomainRepository domainRepository;
    private final UrlRepository urlRepository;

    @GetMapping("test")
    public void test() {
        long start = System.currentTimeMillis();
        List<Domain> domains = domainRepository.findAll();
        long end = System.currentTimeMillis();
        System.out.println((end - start));


        start = System.currentTimeMillis();
        urlRepository.countAllByDomain(domainRepository.findByDomain("dl.dropboxusercontent.com").get());
        end = System.currentTimeMillis();
        System.out.println((end - start));
    }
}
