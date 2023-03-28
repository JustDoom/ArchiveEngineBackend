package com.imjustdoom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String domain;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domain")
    private List<Url> urls = new ArrayList<>();

    public Domain(String domain) {
        this.domain = domain;
    }
}
