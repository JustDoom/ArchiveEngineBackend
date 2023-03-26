package com.imjustdoom;

import lombok.Setter;

public class ApiUrlBuilder {

    private final String apiUrl = "https://web.archive.org/web/timemap/json?url=";

    private String domain = "";
    private String limit = "";

    public String build() {
        return apiUrl + domain + limit;
    }

    public ApiUrlBuilder setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public ApiUrlBuilder setLimit(int limit) {
        this.limit = "&limit=" + limit;
        return this;
    }
}
