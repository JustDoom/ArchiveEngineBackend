package com.imjustdoom.model;

import jakarta.persistence.*;

@Entity
public class FailedRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int statusCode;

    @Column(nullable = false)
    private int page;

    @ManyToOne(fetch = FetchType.LAZY)
    private TopDomain topDomain;

    public FailedRequest() {}

    public FailedRequest(int statusCode, int page, TopDomain topDomain) {
        this.statusCode = statusCode;
        this.page = page;
        this.topDomain = topDomain;
    }
}
