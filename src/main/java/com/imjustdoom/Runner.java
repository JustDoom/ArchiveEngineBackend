package com.imjustdoom;

import com.imjustdoom.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private UrlService urlService;

    @Override
    public void run(String... args) {
        Timestamp ts = new Timestamp(2014, 1, 1, 0, 0, 0, Timestamp.Time.HOUR);

        new ScanDomain("dl.dropboxusercontent.com", ts, 100, this.urlService).startScanning();
    }
}