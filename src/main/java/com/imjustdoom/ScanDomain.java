package com.imjustdoom;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ScanDomain {

    private final String apiUrl = "https://web.archive.org/web/timemap/json?url=dl.dropboxusercontent.com";

    private final String domain;
    private Timestamp timestamp;

    public ScanDomain(String domain, Timestamp timestamp) {
        this.domain = domain;
        this.timestamp = timestamp;
    }

    public void startScanning() {
        checkForExistingSave();

        getLinks();
    }

    private void checkForExistingSave() {
        if (Main.instance.getSaving().getDomain().equals(this.domain)) {
            this.timestamp = new Timestamp(Main.instance.getSaving().getTimestamp());
        }
    }

    private void getLinks() {
        try {
            System.out.println(new ApiUrlBuilder().setLimit(841).setDomain(this.domain).setFrom(this.timestamp.toString()).setTo(this.timestamp.toString()).build());
            String response = readUrl(new ApiUrlBuilder().setLimit(841).setDomain(this.domain).setFrom(this.timestamp.toString()).setTo(this.timestamp.toString()).build());
            JsonElement element = JsonParser.parseString(response);

            for (int i = 1; i < element.getAsJsonArray().size(); i++) {
                JsonElement urlInfo = element.getAsJsonArray().get(i);
                Main.instance.getDatabase().addLinkIfNotExists(
                        urlInfo.getAsJsonArray().get(0).getAsString(),
                        urlInfo.getAsJsonArray().get(1).getAsString(),
                        urlInfo.getAsJsonArray().get(2).getAsString(),
                        urlInfo.getAsJsonArray().get(3).getAsString(),
                        urlInfo.getAsJsonArray().get(5).getAsString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
