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
    private Domain domain;

    public FailedRequest() {}

    public FailedRequest(Domain domain) {
        this.domain = domain;
    }
}
