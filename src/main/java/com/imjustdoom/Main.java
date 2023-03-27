package com.imjustdoom;

import com.imjustdoom.storage.Database;
import com.imjustdoom.storage.Saving;
import lombok.Getter;

import java.net.URISyntaxException;

@Getter
public class Main {

    private final Database database;
    private final Saving saving;

    public Main() {
        instance = this;

        this.saving = new Saving();
        try {
            this.saving.loadSaveFile();
        } catch (URISyntaxException exception) {
            System.out.println("Unable to load/create save file");
            System.exit(0);
        }

        this.database = new Database();

        String domain = "dl.dropboxusercontent.com";
        Timestamp ts = new Timestamp(2014, 8, 30, 6, 0, 43);

        new ScanDomain(domain, ts, 1000).startScanning();
    }

    public static void main(String[] args) {
        new Main();
    }

    public static Main instance;
}