package com.imjustdoom.indexer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imjustdoom.exception.HttpStatusException;
import com.imjustdoom.model.Domain;
import com.imjustdoom.model.TopDomain;
import com.imjustdoom.model.Url;
import com.imjustdoom.service.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class IndexDomain {
    private final TopDomain topDomainModel;

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

    public void startScanning() {
        try {
            int pages = Integer.parseInt(readUrl(String.format("https://web.archive.org/cdx/search/cdx?url=%s&matchType=domain&showNumPages=true", this.topDomainModel.getDomain())));
            System.out.println("Pages " + pages);
            for (int i = 0; i < pages; i++) {
                String url = String.format("https://web.archive.org/cdx/search/cdx?url=%s&matchType=domain&collapse=urlkey&output=json&fl=original,timestamp,endtimestamp&page=%s", this.topDomainModel.getDomain(), i); //fl=original,mimetype,timestamp,endtimestamp,statuscode,groupcount,uniqcount,digest&page=%s
                System.out.println(url);
                try {
                    String response = readUrl(url);
//                System.out.println(response);

                    long start = System.currentTimeMillis();
                    JsonElement element = JsonParser.parseString(response);
                    System.out.println("Parsed for " + (System.currentTimeMillis() - start) + "ms");

                    System.out.println(element.getAsJsonArray().size());

                    Map<String, Domain> domainMap = new HashMap<>();

                    List<Url> urlList = new ArrayList<>();
                    // TODO: run on another thread so it can make a request while this is running
                    for (int j = 1; j < element.getAsJsonArray().size(); j++) {
                        JsonElement urlInfo = element.getAsJsonArray().get(j);

                        String foundUrl = urlInfo.getAsJsonArray().get(0).getAsString();
                        if (foundUrl.length() > 2048) {
                            System.out.println("Tooo big :(");
                            continue;
                        }

                        // Would like to figure out why. I think the collapse quary in the url should filter them out and this probably gets slow...
                        if (this.urlService.exists(foundUrl)) {
                            System.out.println("Duplicate :( - " + foundUrl);
                            continue;
                        }

                        String cleaned = cleanUrl(foundUrl);

                        if (!domainMap.containsKey(cleaned)) {
                            domainMap.put(cleaned, this.domainService.getDomain(cleaned).orElseGet(() -> this.domainService.addDomain(cleaned, this.topDomainModel)));
                        }

                        urlList.add(new Url(
                                foundUrl,
                                urlInfo.getAsJsonArray().get(1).getAsLong(),
                                urlInfo.getAsJsonArray().get(2).getAsLong(),
                                domainMap.get(cleaned)));
                    }

                    this.urlService.addAllUrl(urlList);
                    this.meilisearchService.indexProducts(urlList);
                } catch (HttpStatusException e) {
                    this.failedRequestService.addFailedRequest(e.getCode(), i, this.topDomainModel);
                }
            }
        } catch (HttpStatusException e) {
            System.err.println("There was an error when making the request to the API. Error Code: " + e.getCode());
        } catch (Exception e) {
            System.err.println("There was an error trying to fetch page information");
            e.printStackTrace();
        }
    }

    @NotNull
    private static String cleanUrl(String foundUrl) {
        String cleaned = foundUrl.replaceFirst("^https?://", "");
        int slashIndex = cleaned.indexOf('/');
        if (slashIndex != -1) {
            cleaned = cleaned.substring(0, slashIndex);
        }
        int questionIndex = cleaned.indexOf('?');
        if (questionIndex != -1) {
            cleaned = cleaned.substring(0, questionIndex);
        }

        int colonIndex = cleaned.lastIndexOf(':');
        if (colonIndex != -1 && colonIndex > cleaned.lastIndexOf(']')) {
            cleaned = cleaned.substring(0, colonIndex);
        }
        return cleaned;
    }

    private String readUrl(String urlString) throws IOException, HttpStatusException {
        URL url = new URL(urlString);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setFollowRedirects(false);
        huc.setConnectTimeout(30 * 1000); // 30 seconds
        huc.setRequestMethod("GET");
        huc.setRequestProperty("User-Agent",  "Wayback Engine - In Development (justdoomdev@gmail.com)");

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
            return buffer.toString().trim(); // Trim to remove the last newline
        }
    }
}
