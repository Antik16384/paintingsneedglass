package net.antik16384.paintingsneedglass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("paintingsneedglass.json");

    public boolean modEnabled = true;
    public boolean hideSideTexture = true;

    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static Config load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, Config.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Config defaultConfig = new Config();
        defaultConfig.save();
        return defaultConfig;
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}