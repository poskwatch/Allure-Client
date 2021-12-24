package vip.allureclient.base.config;

import com.google.gson.JsonObject;
import vip.allureclient.AllureClient;

import java.io.File;

public class Config implements ConfigurableObject {

    private final File file;

    public Config(String fileName) {
        this.file = new File(String.format("%s/%s%s", AllureClient.getInstance().getConfigManager().CONFIG_FOLDER.getPath(), fileName,
                AllureClient.getInstance().getConfigManager().FILE_EXTENSION));
    }

    @Override
    public JsonObject save() {
        JsonObject object = new JsonObject();
        JsonObject modulesObject = new JsonObject();
        AllureClient.getInstance().getModuleManager().getModules.get().forEach(module -> modulesObject.add(module.getModuleName(), module.save()));
        object.add("modules", modulesObject);
        return object;
    }

    @Override
    public void load(JsonObject jsonObject) {
        if (jsonObject.has("modules")) {
            JsonObject modulesJson = jsonObject.getAsJsonObject("modules");
            AllureClient.getInstance().getModuleManager().getModules.get().forEach(module -> {
                if (modulesJson.has(module.getModuleName())) {
                    module.load(modulesJson.getAsJsonObject(module.getModuleName()));
                }
            });
        }
    }

    public File getFile() {
        return file;
    }
}
