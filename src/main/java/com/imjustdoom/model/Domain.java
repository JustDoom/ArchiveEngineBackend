package com.imjustdoom.model;

import com.imjustdoom.indexer.Timestamp;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String domain;

    @Column
    private String timestamp;

    @Column
    private Timestamp.Time time;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "domain")
    private List<Url> urls = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "domain")
    private List<FailedRequest> failedRequests = new ArrayList<>();

    public Domain() {}

    public Domain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }

    public Timestamp.Time getTime() {
        return time;
    }

    public String getTimestamp() {
        return timestamp;
    }


    public void setTime(Timestamp.Time time) {
        this.time = time;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
