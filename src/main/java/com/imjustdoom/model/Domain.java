package com.imjustdoom.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Domain {
    @Id
    @Column(nullable = false, unique = true)
    private String domain;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "domain")
    private List<Url> urls = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "domain")
    private List<FailedRequest> failedRequests = new ArrayList<>();

    // For saving current page number if a restart happens
    @Column
    private Integer pageNumber;

    // Total pages found when starting a scan
    @Column
    private Integer totalPages;

    // Last date/time a scan finished
    @Column
    private LocalDateTime lastScanned;

    // Is a scan currently running, used to restart scans when a restart happens
    @Column(nullable = false)
    private boolean running;

    public Domain() {}

    public Domain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }
}
