package com.imjustdoom.indexer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imjustdoom.exception.HttpStatusException;
import com.imjustdoom.model.Domain;
import com.imjustdoom.model.Url;
import com.imjustdoom.service.DomainService;
import com.imjustdoom.service.FailedRequestService;
import com.imjustdoom.service.MeilisearchService;
import com.imjustdoom.service.UrlService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IndexDomain {
    private final String domain;
    private final Timestamp timestamp;
    private final String stopIndexingTimestamp;
    private final int limit;
    private final Domain domainModel;

    private final UrlService urlService;
    private final DomainService domainService;
    private final FailedRequestService failedRequestService;
    private final MeilisearchService meilisearchService;

    public IndexDomain(String domain, Timestamp timestamp, String stopIndexingTimestamp, int limit, UrlService urlService, DomainService domainService, FailedRequestService failedRequestService, MeilisearchService meilisearchService) {
        this.domain = domain;
        this.timestamp = timestamp;
        this.stopIndexingTimestamp = stopIndexingTimestamp;
        this.limit = limit;
        this.urlService = urlService;
        this.domainService = domainService;
        this.failedRequestService = failedRequestService;
        this.meilisearchService = meilisearchService;

        // Create the domain in the database if it doesn't exist
        Optional<Domain> domainOptional = this.domainService.getDomain(domain);
        if (domainOptional.isEmpty()) {
            this.domainService.addDomain(domain);
            this.domainModel = this.domainService.getDomain(domain).get();
        } else {
            this.domainModel = domainOptional.get();
        }
    }

    public void startScanning() {

        Timestamp.Time one = null;
        int num = -1;

//        // try the ones that failed again
//        List<FailedRequest> failedUrls = this.urlService.getFailedUrls();
//        for (FailedRequest failedRequest : failedUrls) {
//            System.out.println("Trying failed request: " + failedRequest.getTimestamp() + " " + failedRequest.getTime() + " " + failedRequest.getDomain().getDomain());
//            if (getLinks(failedRequest.getDomain().getDomain(), failedRequest.getTimestamp().substring(0, failedRequest.getTime().getSub()), failedRequest.getTime())) {
//                // Store what the too large one was
//                one = this.timestamp.getTimeType();
//                num = this.timestamp.get(one);
//
//                // Go down one time
//                this.timestamp.setTimeType(this.timestamp.goDownOneTime(this.timestamp.getTimeType()));
//            } else { // If it is smaller than the limit
//                // Go up one time if
//                if (one == this.timestamp.getTimeType() && num == this.timestamp.get(this.timestamp.getTimeType()))
//                    this.timestamp.setTimeType(this.timestamp.goUpOneTime(this.timestamp.getTimeType()));
//                this.timestamp.plus(this.timestamp.getTimeType(), 1);
//            }
//        }

        one = null;
        num = -1;

        try {
            int pages = Integer.parseInt(readUrl(String.format("https://web.archive.org/cdx/search/cdx?url=%s&matchType=domain&showNumPages=true", this.domain)));
            System.out.println("Pages " + pages);
            for (int i = 0; i < pages; i++) {
                String url = String.format("https://web.archive.org/cdx/search/cdx?url=%s&matchType=domain&collapse=urlkey&output=json&fl=original,mimetype,timestamp,endtimestamp,statuscode,groupcount,uniqcount,digest&page=%s", this.domain, i);
                System.out.println(url);
                try {
                    String response = readUrl(url);
//                System.out.println(response);

                    JsonElement element = JsonParser.parseString(response);

                    System.out.println(element.getAsJsonArray().size());

                    List<Url> urlList = new ArrayList<>();
                    // TODO: run on another thread so it can make a request while this is running
                    for (int j = 1; j < element.getAsJsonArray().size(); j++) {
                        JsonElement urlInfo = element.getAsJsonArray().get(j);
                        urlList.add(new Url(
                                urlInfo.getAsJsonArray().get(0).getAsString(),
                                urlInfo.getAsJsonArray().get(1).getAsString(),
                                urlInfo.getAsJsonArray().get(2).getAsString(),
                                urlInfo.getAsJsonArray().get(3).getAsString(),
                                urlInfo.getAsJsonArray().get(4).getAsString(),
                                urlInfo.getAsJsonArray().get(7).getAsString(), this.domainModel));
                    }

                    this.urlService.addAllUrl(urlList);
                    this.meilisearchService.indexProducts(urlList);
                } catch (HttpStatusException e) {
                    this.failedRequestService.addFailedRequest(this.timestamp.toString(), this.timestamp.getTimeType(), this.domainModel);
                }
            }
        } catch (HttpStatusException e) {
            System.err.println("There was an error when making the request to the API. Error Code: " + e.getCode());
        } catch (Exception e) {
            System.err.println("There was an error trying to fetch page information");
            e.printStackTrace();
        }

        // super messy, gotta fix it
        // TODO: Make it stop after a certain date, for now it's fine for testing
//        while (!this.timestamp.toString().equals(stopIndexingTimestamp)) {
//            if (getLinks(this.domain, this.timestamp.toString().substring(0, this.timestamp.getTimeType().getSub()), false)) { // If it is larger than the limit
//                // Store what the too large one was
//                one = this.timestamp.getTimeType();
//                num = this.timestamp.get(one);
//
//                // Go down one time
//                this.timestamp.setTimeType(this.timestamp.goDownOneTime(this.timestamp.getTimeType()));
//            } else { // If it is smaller than the limit
//                // Go up one time if
//                if (one == this.timestamp.getTimeType() && num == this.timestamp.get(this.timestamp.getTimeType())) {
//                    this.timestamp.setTimeType(this.timestamp.goUpOneTime(this.timestamp.getTimeType()));
//                }
//                this.timestamp.plus(this.timestamp.getTimeType(), 1);
//            }
//
//            this.domainModel.setTime(this.timestamp.getTimeType());
//            this.domainModel.setTimestamp(this.timestamp.toString());
//            this.domainService.saveDomain(this.domainModel);
//        }
    }

    private boolean getLinks(String domain, String timestamp, boolean firstAttempt) {
        try {
            String url = new ApiUrlBuilder().setLimit(this.limit).setDomain(domain).setFrom(timestamp).setTo(timestamp).build();
            System.out.println(url);
            String response = readUrl(url);
            JsonElement element = JsonParser.parseString(response);

            System.out.println(element.getAsJsonArray().size());

            if (element.getAsJsonArray().size() >= this.limit) {
                return true;
            }

            List<Url> urlList = new ArrayList<>();
            // TODO: run on another thread so it can make a request while this is running
            for (int i = 1; i < element.getAsJsonArray().size(); i++) {
                JsonElement urlInfo = element.getAsJsonArray().get(i);
                urlList.add(new Url(
                        urlInfo.getAsJsonArray().get(0).getAsString(),
                        urlInfo.getAsJsonArray().get(1).getAsString(),
                        urlInfo.getAsJsonArray().get(2).getAsString(),
                        urlInfo.getAsJsonArray().get(3).getAsString(),
                        urlInfo.getAsJsonArray().get(4).getAsString(),
                        urlInfo.getAsJsonArray().get(7).getAsString(), this.domainModel));
            }

            this.urlService.addAllUrl(urlList);
            this.meilisearchService.indexProducts(urlList);
        } catch (Exception exception) {
            if (firstAttempt) {
                System.out.println("Attempt 2");
                this.timestamp.setTimeType(this.timestamp.goDownOneTime(this.timestamp.getTimeType()));
                getLinks(domain, timestamp, false);
            } else {
                exception.printStackTrace();
                this.failedRequestService.addFailedRequest(this.timestamp.toString(), this.timestamp.getTimeType(), this.domainModel);
            }
        }

        return false;
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
