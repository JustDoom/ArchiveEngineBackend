package com.imjustdoom;

import com.imjustdoom.model.Domain;
import com.imjustdoom.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private UrlService urlService;

    @Override
    public void run(String... args) {

        // This is where th indexer starts

        // TODO: Make this configurable
        String domain = "dl.dropboxusercontent.com";

        // Check if domain exists and if it does load its data
        Optional<Domain> optionalDomain = this.urlService.getDomain(domain);
        Timestamp ts;
        if (optionalDomain.isPresent() && optionalDomain.get().getTimestamp() != null && optionalDomain.get().getTime() != null) {
            ts = new Timestamp(optionalDomain.get().getTimestamp(), optionalDomain.get().getTime());

        } else {
            ts = new Timestamp(2014, 1, 1, 0, 0, 0, Timestamp.Time.MONTH);

        }

        // Create the indexer
        new ScanDomain(domain, ts, 10000, this.urlService).startScanning();
    }
}