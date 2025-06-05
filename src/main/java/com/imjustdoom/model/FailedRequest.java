package com.imjustdoom.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class FailedRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int statusCode;

    @Column(nullable = false)
    private int page;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int failCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private TopDomain topDomain;

    public FailedRequest() {}

    public FailedRequest(int statusCode, int page, TopDomain topDomain) {
        this.statusCode = statusCode;
        this.page = page;
        this.topDomain = topDomain;
    }

    public int getPage() {
        return this.page;
    }

    public int getFailCount() {
        return this.failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }
}
