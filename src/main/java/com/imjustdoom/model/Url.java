package com.imjustdoom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Size;

/**
 * Each unique url that gets indexed
 */
@Entity
@Table(indexes = {
//        @Index(name = "urlIndex", columnList = "url", unique = true),
        @Index(name = "urlHashIndex", columnList = "urlHash", unique = true)
})
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Do not store domain or subdomain here maybe
    @Column(nullable = false, columnDefinition="VARCHAR(2048)", unique = true)
    @Size(max = 2048, message = "URL must not be more than 2048 characters")
    @URL(message = "Invalid URL")
    private String url;

    @Column(nullable = false, length = 64, unique = true)
    private String urlHash;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private Long endTimestamp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Domain domain;

    public Url() {}

    public Url(String url, String hash, Long timestamp, Long endTimestamp, Domain domain) {
        this.url = url;
        this.urlHash = hash;
        this.timestamp = timestamp;
        this.endTimestamp = endTimestamp;
        this.domain = domain;
    }

    public Long getId() {
        return this.id;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUrlHash() {
        return this.urlHash;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public Long getEndTimestamp() {
        return this.endTimestamp;
    }

    public Domain getDomain() {
        return this.domain;
    }
}
