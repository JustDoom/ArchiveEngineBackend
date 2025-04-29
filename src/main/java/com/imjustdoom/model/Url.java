package com.imjustdoom.model;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"url", "mimeType", "timestamp", "endTimestamp", "statusCode", "domain_id"}))
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition="TEXT")
    @org.hibernate.annotations.Index(name = "idx_url")
    private String url;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private String timestamp;

    @Column(nullable = false)
    private String endTimestamp;

    @Column(nullable = false)
    private String statusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private Domain domain;

    public Url() {}

    public Url(String url, String mimeType, String timestamp, String endTimestamp, String statusCode, Domain domain) {
        this.url = url;
        this.mimeType = mimeType;
        this.timestamp = timestamp;
        this.endTimestamp = endTimestamp;
        this.statusCode = statusCode;
        this.domain = domain;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return this.url;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getEndTimestamp() {
        return this.endTimestamp;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public Domain getDomain() {
        return this.domain;
    }
}
