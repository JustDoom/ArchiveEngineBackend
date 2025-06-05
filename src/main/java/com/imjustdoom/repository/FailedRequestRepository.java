package com.imjustdoom.repository;

import com.imjustdoom.model.TopDomain;
import com.imjustdoom.model.FailedRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedRequestRepository extends JpaRepository<FailedRequest, Long> {
    int countAllByTopDomain(TopDomain topDomain);
    List<FailedRequest> findAllByTopDomain(TopDomain topDomain, Pageable page);
}