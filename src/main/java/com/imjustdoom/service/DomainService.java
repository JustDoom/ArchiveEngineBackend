package com.imjustdoom.service;

import com.imjustdoom.model.Domain;
import com.imjustdoom.model.TopDomain;
import com.imjustdoom.repository.DomainRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
@Service
public class DomainService {
    private final DomainRepository domainRepository;

    public DomainService(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public Domain addDomain(String domain, TopDomain topDomain) {
        return this.domainRepository.save(new Domain(domain, topDomain));
    }

    public Optional<Domain> getDomain(String domain) {
        return this.domainRepository.findByDomain(domain);
    }

    public void saveDomain(Domain topDomain) {
        this.domainRepository.save(topDomain);
    }
}
