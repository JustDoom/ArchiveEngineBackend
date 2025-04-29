package com.imjustdoom.model;

import com.imjustdoom.indexer.Timestamp;
import jakarta.persistence.*;

@Entity
public class FailedRequest {
    public FailedRequest() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    private Domain domain;

    @Column(nullable = false)
    private Timestamp.Time time;

    public FailedRequest(String timestamp, Timestamp.Time time, Domain domain) {
        this.timestamp = timestamp;
        this.domain = domain;
        this.time = time;
    }
}
