package com.imjustdoom;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

@Getter
public class Saving {

    private File saveFile;
    private JsonObject save;

    private String domain = "";
    private String timestamp = "";

    public void loadSettings() {
        this.domain = getString("domain", this.domain);
        this.timestamp = getString("timestamp", this.timestamp);
    }

    private boolean getBoolean(String path, boolean def) {
        JsonElement element = getJsonElement(this.save, path);
        if (element == null || element.isJsonNull()) {
            setJsonElement(this.save, path, def);
            return def;
        }
        return element.getAsBoolean();
    }

    private String getString(String path, String def) {
        JsonElement element = getJsonElement(this.save, path);
        if (element == null || element.isJsonNull()) {
            setJsonElement(this.save, path, def);
            return def;
        }
        return element.getAsString();
    }

    private int getInt(String path, int def) {
        JsonElement element = getJsonElement(this.save, path);
        if (element == null || element.isJsonNull()) {
            setJsonElement(this.save, path, def);
            return def;
        }
        return element.getAsInt();
    }

    public void loadSaveFile() throws URISyntaxException {
        String path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        this.saveFile = new File(path + "/save.json");
        if (!this.saveFile.exists()) {
            try {
                this.saveFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        try {
            JsonReader jsonReader = new JsonReader(new FileReader(this.saveFile));
            JsonElement element = JsonParser.parseReader(jsonReader);
            if (element.isJsonNull()) {
                this.save = new JsonObject();
            } else {
                this.save = element.getAsJsonObject();
            }

            loadSettings();

            saveFile();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void saveFile() throws IOException {
        FileWriter writer = new FileWriter(this.saveFile);
        new GsonBuilder().setPrettyPrinting().create().toJson(this.save, writer);
        writer.flush();
        writer.close();
    }

    private JsonElement getJsonElement(JsonElement json, String path) {

        String[] parts = path.split("\\.|\\[|\\]");
        JsonElement result = json;

        for (String key : parts) {

            key = key.trim();
            if (key.isEmpty())
                continue;

            if (result == null) {
                result = JsonNull.INSTANCE;
                break;
            }

            if (result.isJsonObject()) {
                result = ((JsonObject) result).get(key);
            } else if (result.isJsonArray()) {
                int ix = Integer.parseInt(key) - 1;
                result = ((JsonArray) result).get(ix);
            } else break;
        }

        return result;
    }

    private void setJsonElement(JsonElement json, String path, boolean value) {

        String[] parts = path.split("\\.|\\[|\\]");

        JsonElement section = json;

        for (int i = 0; i < parts.length; i++) {

            if (i + 1 == parts.length) {
                section.getAsJsonObject().addProperty(parts[i], value);
                return;
            }

            if (!section.getAsJsonObject().has(parts[i])) {
                section.getAsJsonObject().add(parts[i], new JsonObject());
            }

            section = section.getAsJsonObject().get(parts[i]);
        }
    }

    private void setJsonElement(JsonElement json, String path, String value) {

        String[] parts = path.split("\\.|\\[|\\]");

        JsonElement section = json;

        for (int i = 0; i < parts.length; i++) {

            if (i + 1 == parts.length) {
                section.getAsJsonObject().addProperty(parts[i], value);
                return;
            }

            if (!section.getAsJsonObject().has(parts[i])) {
                section.getAsJsonObject().add(parts[i], new JsonObject());
            }

            section = section.getAsJsonObject().get(parts[i]);
        }
    }

    private void setJsonElement(JsonElement json, String path, int value) {

        String[] parts = path.split("\\.|\\[|\\]");

        JsonElement section = json;

        for (int i = 0; i < parts.length; i++) {

            if (i + 1 == parts.length) {
                section.getAsJsonObject().addProperty(parts[i], value);
                return;
            }

            if (!section.getAsJsonObject().has(parts[i])) {
                section.getAsJsonObject().add(parts[i], new JsonObject());
            }

            section = section.getAsJsonObject().get(parts[i]);
        }
    }
}
