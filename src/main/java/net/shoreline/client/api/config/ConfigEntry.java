package net.shoreline.client.api.config;

import com.google.gson.JsonObject;

/**
 * Interface for objects that save their own {@link JsonObject}.
 */
public interface ConfigEntry
{
    void fromJson(JsonObject object);

    JsonObject toJson();
}