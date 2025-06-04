package com.imjustdoom.service;

import com.imjustdoom.model.TopDomain;
import com.imjustdoom.repository.TopDomainRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
@Service
public class TopDomainService {
    private final TopDomainRepository topDomainRepository;

    public TopDomainService(TopDomainRepository topDomainRepository) {
        this.topDomainRepository = topDomainRepository;
    }

    public TopDomain addDomain(String domain) {
        return this.topDomainRepository.save(new TopDomain(domain));
    }

    public Optional<TopDomain> getDomain(String domain) {
        return this.topDomainRepository.findByTopDomain(domain);
    }

    public void saveDomain(TopDomain topDomain) {
        this.topDomainRepository.save(topDomain);
    }
}
