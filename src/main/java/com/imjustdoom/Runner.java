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

        String domain = "";
        String timestamp = "20000101000000";
        String stopIndexingTimestamp = "99990101000000";
        boolean timestampOverride = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--disable" -> {
                    System.out.println("Disabling indexer");
                    return;
                }
                case "--domain" -> domain = args[i + 1];
                case "--timestamp" -> timestamp = args[i +  1];
                case "--stopIndexingTimestamp" -> stopIndexingTimestamp = args[i + 1];
                case "--timestampOverride" -> timestampOverride = true;
            }
        }

        if (domain.isEmpty()) {
            System.out.println("Please provide a domain to scan");
            return;
        }

        // This is where th indexer starts

        // Check if domain exists and if it does load its data
        Optional<Domain> optionalDomain = this.urlService.getDomain(domain);
        Timestamp ts;
        if (timestampOverride) {
            ts = new Timestamp(timestamp, Timestamp.Time.MONTH);
        } else if (optionalDomain.isPresent() && optionalDomain.get().getTimestamp() != null && optionalDomain.get().getTime() != null) {
            ts = new Timestamp(optionalDomain.get().getTimestamp(), optionalDomain.get().getTime());
        } else {
            ts = new Timestamp(timestamp, Timestamp.Time.MONTH);
        }

        // Create the indexer
        new IndexDomain(domain, ts, stopIndexingTimestamp, 10000, this.urlService).startScanning();
    }
}