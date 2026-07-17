package net.shoreline.client.api.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * An interface for objects that adds
 * a {@link JsonElement} to a {@link JsonObject},
 * Usually children of an object implementing {@link ConfigEntry}.
 */
public interface ConfigElement
{
    void fromJson(JsonElement element);

    JsonElement toJson();

    default boolean isTransient()
    {
        return false;
    }

    default JsonElement parse(String string)
    {
        JsonElement result = null;
        try (JsonReader reader = new JsonReader(new StringReader(string)))
        {
            result = JsonParser.parseReader(reader);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }
}