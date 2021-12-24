package vip.allureclient.base.config;

import com.google.gson.JsonObject;

public interface ConfigurableObject {
    JsonObject save();
    void load(JsonObject jsonObject);
}
