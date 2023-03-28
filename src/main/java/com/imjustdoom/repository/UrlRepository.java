package com.imjustdoom.repository;

import com.imjustdoom.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findById(long id);

    boolean existsById(long id);

    List<Url> findAllByUrlIsContainingIgnoreCase(String keyword);
}
