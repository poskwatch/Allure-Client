package vip.allureclient.base.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.client.ClientExitEvent;
import vip.allureclient.impl.event.world.WorldLoadEvent;

import java.io.*;
import java.util.Objects;

public class ConfigManager {

    public final File CONFIG_FOLDER = new File(AllureClient.getInstance().getFileManager().getClientDirectory().getPath() + "/Configs");
    public final String FILE_EXTENSION = ".json";
    private boolean hasLoadedDefault = false;

    public ConfigManager() {
        if (!CONFIG_FOLDER.exists()) {
            if (CONFIG_FOLDER.mkdirs()) {
                System.out.println("Created config directory");
            }
            else
                System.out.println("Couldn't create config directory");
        }
        this.onClientExit = (event -> saveDefaultConfig());
        this.onWorldLoadEvent = (event -> {
            if (!hasLoadedDefault) {
                loadDefaultConfig();
                ChatUtil.sendMessageToPlayer("Loaded settings from: " + CONFIG_FOLDER.listFiles()[1].lastModified());
                hasLoadedDefault = true;
            }
        });
        Wrapper.getEventManager().subscribe(this);
    }

    public boolean loadConfig(String configName) {
        if (configName == null)
            return false;
        configName = configName + ".json";
        for (File file : Objects.requireNonNull(CONFIG_FOLDER.listFiles())) {
            if (configName.equals(file.getName())) {
                try {
                    FileReader reader = new FileReader(file);
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(reader);
                    new Config(configName).load(jsonObject);
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean saveConfig(String configName) {
        if (configName == null)
            return false;
        Config config = new Config(configName);
        String contentPrettyPrint = (new GsonBuilder()).setPrettyPrinting().create().toJson(config.save());
        try {
            FileWriter fileWriter = new FileWriter(config.getFile());
            fileWriter.write(contentPrettyPrint);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveDefaultConfig() {
        if (saveConfig("default")) {
            System.out.println("Saved default settings");
        }
        else {
            System.out.println("Couldn't save default settings");
        }
    }

    public void loadDefaultConfig() {
        if (loadConfig("default")) {
            System.out.println("Loaded default settings");
        }
        else {
            System.out.println("Couldn't load default settings");
        }
    }

    @EventListener
    EventConsumer<WorldLoadEvent> onWorldLoadEvent;

    @EventListener
    EventConsumer<ClientExitEvent> onClientExit;
}
