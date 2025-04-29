package com.imjustdoom.indexer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imjustdoom.model.Domain;
import com.imjustdoom.model.Url;
import com.imjustdoom.service.DomainService;
import com.imjustdoom.service.FailedRequestService;
import com.imjustdoom.service.MeilisearchService;
import com.imjustdoom.service.UrlService;

import java.io.BufferedReader;
import java.io.InputStream;
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

        // super messy, gotta fix it
        // TODO: Make it stop after a certain date, for now it's fine for testing
        while (!this.timestamp.toString().equals(stopIndexingTimestamp)) {
            if (getLinks(this.domain, this.timestamp.toString().substring(0, this.timestamp.getTimeType().getSub()))) { // If it is larger than the limit
                // Store what the too large one was
                one = this.timestamp.getTimeType();
                num = this.timestamp.get(one);

                // Go down one time
                this.timestamp.setTimeType(this.timestamp.goDownOneTime(this.timestamp.getTimeType()));
            } else { // If it is smaller than the limit
                // Go up one time if
                if (one == this.timestamp.getTimeType() && num == this.timestamp.get(this.timestamp.getTimeType())) {
                    this.timestamp.setTimeType(this.timestamp.goUpOneTime(this.timestamp.getTimeType()));
                }
                this.timestamp.plus(this.timestamp.getTimeType(), 1);
            }

            this.domainModel.setTime(this.timestamp.getTimeType());
            this.domainModel.setTimestamp(this.timestamp.toString());
            this.domainService.saveDomain(this.domainModel);
        }
    }

    private boolean getLinks(String domain, String timestamp) {
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
                urlList.add(new Url(urlInfo.getAsJsonArray().get(0).getAsString(), urlInfo.getAsJsonArray().get(1).getAsString(), urlInfo.getAsJsonArray().get(2).getAsString(), urlInfo.getAsJsonArray().get(3).getAsString(), urlInfo.getAsJsonArray().get(5).getAsString(), this.domainModel));
            }

            this.urlService.addAllUrl(urlList);
            this.meilisearchService.indexProducts(urlList);
        } catch (Exception exception) {
            exception.printStackTrace();
            this.failedRequestService.addFailedRequest(this.timestamp.toString(), this.timestamp.getTimeType(), this.domainModel);
        }

        return false;
    }

    private String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(false);
            huc.setConnectTimeout(60 * 1000);
            huc.setRequestMethod("GET");
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            huc.connect();
            InputStream input = huc.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
