package com.imjustdoom.model;

import jakarta.persistence.*;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * The full domain, including subdomain, that has been searched.
 */
@Entity
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(max = 255, message = "Domain must not be over 255")
    private String domain;

    @ManyToOne(fetch = FetchType.LAZY)
    private TopDomain topDomain;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "domain")
    private final List<Url> urls = new ArrayList<>();

    protected Domain() {}

    public Domain(String domain, TopDomain topDomain) {
        this.domain = domain;
        this.topDomain = topDomain;
    }

    public Long getId() {
        return this.id;
    }

    public String getDomain() {
        return this.domain;
    }

    public TopDomain getTopDomain() {
        return this.topDomain;
    }
}
