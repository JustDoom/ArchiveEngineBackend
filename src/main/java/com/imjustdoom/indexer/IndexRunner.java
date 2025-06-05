package com.imjustdoom.indexer;

import com.imjustdoom.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IndexRunner implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(IndexRunner.class);

    private final UrlService urlService;
    private final TopDomainService topDomainService;
    private final DomainService domainService;
    private final FailedRequestService failedRequestService;
    private final MeilisearchService meilisearchService;

    public IndexRunner(UrlService urlService, TopDomainService topDomainService, DomainService domainService, FailedRequestService failedRequestService, MeilisearchService meilisearchService) {
        this.urlService = urlService;
        this.topDomainService = topDomainService;
        this.domainService = domainService;
        this.failedRequestService = failedRequestService;
        this.meilisearchService = meilisearchService;
    }

    @Override
    public void run(String... args) {
        String domain = "";
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--disable" -> {
                    LOG.info("Disabling indexer");
                    return;
                }
                case "--domain" -> domain = args[i + 1];
            }
        }

        if (domain.isEmpty()) {
            LOG.info("Please provide a domain to scan");
            return;
        }

        // Create the indexer
        IndexDomain indexer = new IndexDomain(domain, this.urlService, this.topDomainService, this.domainService, this.failedRequestService, this.meilisearchService);
        try {
            indexer.startScanning(10);
            LOG.info("Indexer finished for {}", domain);
        } catch (Exception exception) {
            LOG.error("Failed to run indexer :(");
        }
        indexer.getTopDomainModel().setLastScanned(LocalDateTime.now());
        this.topDomainService.saveDomain(indexer.getTopDomainModel());
    }
}