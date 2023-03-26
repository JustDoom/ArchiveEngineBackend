package com.imjustdoom;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ScanDomain {

    private final String apiUrl = "https://web.archive.org/web/timemap/json?url=dl.dropboxusercontent.com&matchType=prefix&collapse=urlkey&output=json&fl=original%2Cmimetype%2Ctimestamp%2Cendtimestamp%2Cgroupcount%2Cstatuscode&limit=10000&from=2014122216&to=2014122216";

    private final String domain;
    private Timestamp timestamp;

    public ScanDomain(String domain, Timestamp timestamp) {
        this.domain = domain;
        this.timestamp = timestamp;
    }

    public void startScanning() {
        checkForExistingSave();

        String url = new ApiUrlBuilder()
                .setDomain(this.domain)
                .build();

        try {
            String response = readUrl(this.apiUrl);
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

    private void checkForExistingSave() {
        if (Main.instance.getSaving().getDomain().equals(this.domain)) {
            this.timestamp = new Timestamp(Main.instance.getSaving().getTimestamp());
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
