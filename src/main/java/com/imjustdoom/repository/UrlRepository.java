package com.imjustdoom.repository;

import com.imjustdoom.model.Domain;
import com.imjustdoom.model.Url;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findById(long id);

    boolean existsById(long id);

    @Query("SELECT u FROM Url u WHERE LOWER(u.url) LIKE %:keyword%")
    List<Url> findAllByUrlIsContainingIgnoreCase(String keyword, Pageable pageable);

    boolean existsByUrlAndMimeTypeAndTimestampAndEndTimestampAndStatusCode(String url, String mimeType, String timestamp, String endTimestamp, String statusCode);

    int countAllByDomain(Domain domain);

    Optional<Url> findFirstByDomainOrderByTimestampAsc(Domain domain);

    Optional<Url> findFirstByDomainOrderByTimestampDesc(Domain domain);
}
