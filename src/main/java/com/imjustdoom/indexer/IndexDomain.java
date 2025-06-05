package com.imjustdoom.indexer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imjustdoom.UrlUtil;
import com.imjustdoom.exception.HttpStatusException;
import com.imjustdoom.model.Domain;
import com.imjustdoom.model.TopDomain;
import com.imjustdoom.model.Url;
import com.imjustdoom.service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexDomain {
    // 5 times out and 3 seems to work fine
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    private final TopDomain topDomainModel;
    private final Map<String, Domain> domainMap = new ConcurrentHashMap<>();
    private final Cache<String, Boolean> urlSet = CacheBuilder.newBuilder()
            .maximumSize(100000)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();

    private final UrlService urlService;
    private final TopDomainService topDomainService;
    private final DomainService domainService;
    private final FailedRequestService failedRequestService;
    private final MeilisearchService meilisearchService;

    public IndexDomain(String domain, UrlService urlService, TopDomainService topDomainService, DomainService domainService, FailedRequestService failedRequestService, MeilisearchService meilisearchService) {
        this.urlService = urlService;
        this.topDomainService = topDomainService;
        this.domainService = domainService;
        this.failedRequestService = failedRequestService;
        this.meilisearchService = meilisearchService;

        // Create the domain in the database if it doesn't exist
        this.topDomainModel = this.topDomainService.getDomain(domain).orElseGet(() -> this.topDomainService.addDomain(domain));
    }

    public void startScanning(int batchSize) {
        try {
            int pages = Integer.parseInt(readUrl(String.format("https://web.archive.org/cdx/search/cdx?url=%s&matchType=domain&showNumPages=true", this.topDomainModel.getDomain())));

            System.out.printf("Total pages to process for the domain %s: %d%n", this.topDomainModel.getDomain(), pages);

            AtomicInteger completedPages = new AtomicInteger(0);
            for (int i = 0; i < pages; i += batchSize) {
                int count = Math.min(i + batchSize, pages);
                List<CompletableFuture<Void>> batchFutures = new ArrayList<>();

                for (int j = i; j < count; j++) {
                    String url = String.format("https://web.archive.org/cdx/search/cdx?url=%s&matchType=domain&collapse=urlkey&output=json&fl=original,mimetype,timestamp,endtimestamp,statuscode,groupcount,uniqcount,digest&page=%s", this.topDomainModel.getDomain(), j);
                    int finalJ = j;
                    Thread.sleep(50);
                    batchFutures.add(CompletableFuture.runAsync(() -> {
                        try {
                            processPageResponse(readUrl(url), finalJ, pages, completedPages);
                        } catch (Exception e) {
                            if (e instanceof HttpStatusException exception) {
                                this.failedRequestService.addFailedRequest(exception.getCode(), finalJ, this.topDomainModel);
                                return;
                            }
                            System.err.println("Error processing page " + finalJ + ": " + e.getMessage());
                        }
                    }, this.executor));
                }

                CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0])).join();

                System.out.println("Completed batch " + (i / batchSize + 1) + "/" + ((pages + batchSize - 1) / batchSize));
            }

        } catch (Exception e) {
            System.err.println("Error in startScanning: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processPageResponse(String response, int pageNum, int totalPages, AtomicInteger completedPages) {
        try {
            JsonElement element = JsonParser.parseString(response);

            if (element.getAsJsonArray().size() <= 1) {
                System.out.println("No data on page " + pageNum);
                return;
            }

            List<Url> urlList = Collections.synchronizedList(new ArrayList<>());

            // Process URLs in parallel batches
            int urlBatchSize = 1000;
            List<CompletableFuture<Void>> urlFutures = new ArrayList<>();

            for (int j = 1; j < element.getAsJsonArray().size(); j += urlBatchSize) {
                final int startIndex = j;
                final int endIndex = Math.min(j + urlBatchSize, element.getAsJsonArray().size());
                urlFutures.add(CompletableFuture.runAsync(() -> processUrlBatch(element, startIndex, endIndex, urlList)));
            }

            // Wait for all URL processing to complete
            CompletableFuture.allOf(urlFutures.toArray(new CompletableFuture[0])).join();

            if (!urlList.isEmpty()) {
                this.urlService.addAllUrlsTransaction(urlList);
                this.meilisearchService.indexProducts(urlList);
            }

            int completed = completedPages.incrementAndGet();
            System.out.println("Completed page " + (pageNum + 1) + "/" + totalPages + " (" + completed + " total completed) - " + urlList.size() + " URLs processed");
        } catch (Exception e) {
            System.err.println("Error processing response for page " + pageNum + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processUrlBatch(JsonElement element, int startIndex, int endIndex, List<Url> urlList) {
        for (int j = startIndex; j < endIndex; j++) {
            try {
                JsonElement urlInfo = element.getAsJsonArray().get(j);
                String foundUrl = urlInfo.getAsJsonArray().get(0).getAsString();

                // TODO: Handle super long urls
                if (foundUrl.length() > 2048) {
                    continue;
                }

                String urlHash = UrlUtil.generateUrlHash(foundUrl);

                // Check in memory list. Faster than database checks or exception handling probably
                if (this.urlSet.getIfPresent(urlHash) != null) {
                    continue;
                }

                this.urlSet.put(urlHash, true);
                String cleaned = UrlUtil.cleanUrl(foundUrl);

                if (!this.domainMap.containsKey(cleaned)) {
                    this.domainMap.put(cleaned, this.domainService.getDomain(cleaned).orElseGet(() -> this.domainService.addDomain(cleaned, this.topDomainModel)));
                }

                urlList.add(new Url(
                        foundUrl,
                        urlHash,
                        urlInfo.getAsJsonArray().get(2).getAsLong(),
                        urlInfo.getAsJsonArray().get(3).getAsLong(),
                        this.domainMap.get(cleaned)));
            } catch (Exception e) {
                System.err.println("Error processing URL at index " + j + ": " + e.getMessage());
            }
        }
    }

    private String readUrl(String urlString) throws IOException, HttpStatusException {
        System.out.println("Making request to: " + urlString);

        URL url = URI.create(urlString).toURL();
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setFollowRedirects(false);
        huc.setConnectTimeout(30 * 1000);
        huc.setReadTimeout(30 * 1000); // Add read timeout
        huc.setRequestMethod("GET");
        huc.setRequestProperty("User-Agent", "Wayback Engine - In Development (justdoomdev@gmail.com)");

        int responseCode = huc.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new HttpStatusException(responseCode);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(huc.getInputStream()))) {
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append(System.lineSeparator());
            }
            return buffer.toString().trim();
        }
    }
}
