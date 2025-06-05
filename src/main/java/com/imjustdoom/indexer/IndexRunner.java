package com.imjustdoom.indexer;

import com.imjustdoom.model.TopDomain;
import com.imjustdoom.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

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

        if (!domain.isEmpty()) {
            String finalDomain = domain;
            this.topDomainService.getDomain(domain).orElseGet(() -> this.topDomainService.addTopDomain(finalDomain));
        }

        while (true) {
            Optional<TopDomain> topDomainOptional = this.topDomainService.getTopPriorityNull();
            if (topDomainOptional.isEmpty()) {
                topDomainOptional = this.topDomainService.getLastestTopDomain();
                if (topDomainOptional.isEmpty()) {
                    LOG.error("Well it would seem like we can not fetch any domains to index from :(");
                    return;
                }
            }
            TopDomain topDomain = topDomainOptional.get();

            // Create the indexer
            IndexDomain indexer = new IndexDomain(topDomain, this.urlService, this.topDomainService, this.domainService, this.failedRequestService, this.meilisearchService);
            try {
                indexer.startScanning(10);
                LOG.info("Indexer finished for {}", topDomain.getDomain());
            } catch (Exception exception) {
                LOG.error("Failed to run indexer :(");
            }
            indexer.getTopDomainModel().setLastScanned(LocalDateTime.now());
            this.topDomainService.saveDomain(indexer.getTopDomainModel());
        }
    }
}