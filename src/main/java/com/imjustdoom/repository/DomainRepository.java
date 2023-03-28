package com.imjustdoom.repository;

import com.imjustdoom.model.Domain;
import com.imjustdoom.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Integer> {

    Optional<Domain> findById(long id);

    boolean existsByDomain(String domain);

    Optional<Domain> findByDomain(String domain);

    boolean existsById(long id);
}
