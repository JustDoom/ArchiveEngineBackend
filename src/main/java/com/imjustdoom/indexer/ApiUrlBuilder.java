package com.imjustdoom.indexer;

public class ApiUrlBuilder {

    private final String apiUrl = "https://web.archive.org/cdx/search/cdx?url=";
    private final String staticOptions = "&matchType=prefix&collapse=urlkey&output=json&fl=original,mimetype,timestamp,endtimestamp,statusCode,groupcount,uniqcount,digest";

    // Both prefix and domain options could be useful https://github.com/internetarchive/wayback/tree/master/wayback-cdx-server#url-match-scope
    private String domain = "";
    private String limit = "";
    private String to = "";
    private String from = "";

    public String build() {
        return apiUrl + domain + limit + to + from + staticOptions;
    }

    public ApiUrlBuilder setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public ApiUrlBuilder setLimit(int limit) {
        this.limit = "&limit=" + limit;
        return this;
    }

    public ApiUrlBuilder setFrom(String from) {
        this.from = "&from=" + from;
        return this;
    }

    public ApiUrlBuilder setTo(String to) {
        this.to = "&to=" + to;
        return this;
    }
}
