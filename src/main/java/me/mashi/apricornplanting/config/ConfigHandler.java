package me.mashi.apricornplanting.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHandler {
    public boolean enableForAllLeaves = false;

    public static ConfigHandler load() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        ConfigHandler config = new ConfigHandler();
        File configFile = new File("config/apricornplanting.json");
        configFile.getParentFile().mkdirs();

        if (configFile.exists()) {
            try (FileReader fileReader = new FileReader(configFile)) {
                config = gson.fromJson(fileReader, ConfigHandler.class);
            } catch (IOException e) {
                System.out.println("Error reading config file");
            }
        }

        try (FileWriter fileWriter = new FileWriter(configFile)) {
            gson.toJson(config, fileWriter);
        } catch (IOException e) {
            System.out.println("Error writing config file");
        }

        return config;
    }
}