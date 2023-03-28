package com.imjustdoom;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imjustdoom.model.Domain;
import com.imjustdoom.service.UrlService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

public class ScanDomain {

    private final String apiUrl = "https://web.archive.org/web/timemap/json?url=dl.dropboxusercontent.com";

    private final String domain;
    private Timestamp timestamp;
    private final int limit;
    private final Domain domainModel;

    private final UrlService urlService;

    public ScanDomain(String domain, Timestamp timestamp, int limit, UrlService urlService) {
        this.domain = domain;
        this.timestamp = timestamp;
        this.limit = limit;
        this.urlService = urlService;

        // Create the domain in the database if it doesn't exist
        Optional<Domain> domainOptional = this.urlService.getDomain(domain);
        if (domainOptional.isEmpty()) {
            this.urlService.addDomain(domain);
            this.domainModel = this.urlService.getDomain(domain).get();
        } else {
            this.domainModel = domainOptional.get();
        }
    }

    public void startScanning() {

        Timestamp.Time one = null;
        int num = -1;

        // super messy, gotta fix it
        // TODO: Make it stop after a certain date, for now it's fine for testing
        while (true) {
            if (getLinks(this.timestamp.getTimeType())) { // If it is larger than the limit
                // Store what the too large one was
                one = this.timestamp.getTimeType();
                num = this.timestamp.get(one);

                // Go down one time
                this.timestamp.setTimeType(this.timestamp.goDownOneTime(this.timestamp.getTimeType()));
            } else { // If it is smaller than the limit
                // Go up one time if
                if (one == this.timestamp.getTimeType() && num == this.timestamp.get(this.timestamp.getTimeType()))
                    this.timestamp.setTimeType(this.timestamp.goUpOneTime(this.timestamp.getTimeType()));
                this.timestamp.plus(this.timestamp.getTimeType(), 1);
            }

            this.domainModel.setTime(this.timestamp.getTimeType());
            this.domainModel.setTimestamp(this.timestamp.toString());
            this.urlService.saveDomain(this.domainModel);
        }
    }

    private boolean getLinks(Timestamp.Time time) {
        try {
            String response = readUrl(new ApiUrlBuilder().setLimit(this.limit).setDomain(this.domain).setFrom(this.timestamp.toString().substring(0, time.getSub())).setTo(this.timestamp.toString().substring(0, time.getSub())).build());
            JsonElement element = JsonParser.parseString(response);

            if (element.getAsJsonArray().size() >= this.limit) {
                return true;
            }

            // TODO: run on another thread so it can make a request while this is running
            for (int i = 1; i < element.getAsJsonArray().size(); i++) {
                JsonElement urlInfo = element.getAsJsonArray().get(i);
                this.urlService.addUrl(urlInfo.getAsJsonArray().get(0).getAsString(), urlInfo.getAsJsonArray().get(1).getAsString(), urlInfo.getAsJsonArray().get(2).getAsString(), urlInfo.getAsJsonArray().get(3).getAsString(), urlInfo.getAsJsonArray().get(5).getAsString(), this.domainModel);
            }
        } catch (Exception exception) {
            this.urlService.addFailedRequest(this.timestamp.toString(), this.timestamp.getTimeType(), this.domainModel);
        }

        return false;
    }

    private String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
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
