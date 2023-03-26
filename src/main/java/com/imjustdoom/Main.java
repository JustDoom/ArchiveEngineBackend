package com.imjustdoom;

import lombok.Getter;

import java.net.URISyntaxException;
import java.sql.SQLException;

@Getter
public class Main {

    private final Database database;
    private final Saving saving;

    public Main() {
        this.saving = new Saving();
        try {
            this.saving.loadSaveFile();
        } catch (URISyntaxException exception) {
            System.out.println("Unable to load/create save file");
            System.exit(0);
        }

        this.database = new Database();

        for (int i = 0; i < 20000; i++) {
            try {
                database.addLink("https://test", "text/html", "20111212064341", "20111212064341", "200");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        instance = new Main();
    }

    public static Main instance;
}