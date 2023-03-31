package com.imjustdoom.repository;

import com.imjustdoom.model.Domain;
import com.imjustdoom.model.FailedRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedRequestRepository extends JpaRepository<FailedRequest, Long> {

    boolean existsByTimestampAndDomain(String timestamp, Domain domain);

    int countAllByDomain(Domain domain);
}