package com.imjustdoom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"url", "timestamp", "end_timestamp"}))
public class Url {
    @Id
    @Column(nullable = false, columnDefinition="TEXT")
    private String url;

    @Column(nullable = false)
    private String timestamp;

    @Column(nullable = false)
    private String endTimestamp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Domain domain;

    public Url() {}

    public Url(String url, String timestamp, String endTimestamp, Domain domain) {
        this.url = url;
        this.timestamp = timestamp;
        this.endTimestamp = endTimestamp;
        this.domain = domain;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getEndTimestamp() {
        return this.endTimestamp;
    }

    public Domain getDomain() {
        return this.domain;
    }
}
