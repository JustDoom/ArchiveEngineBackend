package com.imjustdoom.repository;

import com.imjustdoom.model.TopDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopDomainRepository extends JpaRepository<TopDomain, Integer> {

//    Optional<TopDomain> findById(long id);

//    boolean existsByDomain(String domain);

    Optional<TopDomain> findByTopDomain(String topDomain);
}
