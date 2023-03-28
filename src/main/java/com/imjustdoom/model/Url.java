package com.imjustdoom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition="TEXT")
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

    public Url(String url, String mimeType, String timestamp, String endTimestamp, String statusCode, Domain domain) {
        this.url = url;
        this.mimeType = mimeType;
        this.timestamp = timestamp;
        this.endTimestamp = endTimestamp;
        this.statusCode = statusCode;
        this.domain = domain;
    }
}
