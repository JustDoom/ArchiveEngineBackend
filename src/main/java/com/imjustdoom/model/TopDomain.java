package com.imjustdoom.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The main domain, such as discord.com, kagi.com or imjustdoom.com. Does not include the subdomain like search.eimerarchive.org
 */
@Entity
public class TopDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(max = 255, message = "Top domain must not be over 255")
    private String topDomain;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "topDomain")
    private final List<Domain> domains = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "topDomain")
    private final List<FailedRequest> failedRequests = new ArrayList<>();

    /**
     * For saving current page number if a restart happens. Set to -1 if no scan is happening
     */
    @Column(nullable = false)
    @ColumnDefault("-1")
    private Integer pageNumber = -1;

    /**
     * Total pages found when starting a scan
     */
    @Column
    private Integer totalPages;

    /**
     * Last date/time a scan finished
     */
    @Column
    private LocalDateTime lastScanned;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int priority = 0;

    protected TopDomain() {}

    public TopDomain(String topDomain) {
        this.topDomain = topDomain;
    }

    public String getDomain() {
        return this.topDomain;
    }

    public Integer getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public LocalDateTime getLastScanned() {
        return this.lastScanned;
    }

    public void setLastScanned(LocalDateTime lastScanned) {
        this.lastScanned = lastScanned;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
