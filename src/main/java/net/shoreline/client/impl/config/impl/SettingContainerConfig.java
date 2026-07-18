package net.shoreline.client.impl.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.shoreline.client.api.registry.Registry;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.util.SettingContainer;
import net.shoreline.client.impl.config.AbstractConfig;
import net.shoreline.client.impl.config.util.IOUtils;
import net.shoreline.loader.Loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class SettingContainerConfig<T extends SettingContainer>
        extends AbstractConfig
{
    private final Registry<T> registry;

    public SettingContainerConfig(Path directory, String pathIn, Registry<T> registry) throws IOException
    {
        super(directory, pathIn);
        this.registry = registry;
    }

    @Override
    public void saveFile() throws IOException
    {
        JsonObject object = new JsonObject();
        for (T container : registry.getCollection())
        {
            JsonObject cObject = new JsonObject();
            for (Setting<?> setting : getSettings(container))
            {
                cObject.add(setting.getName(), setting.toJson());
            }

            object.add(container.getName(), cObject);
        }

        IOUtils.writeFile(getPath(), GSON.toJson(object));
    }

    @Override
    public void loadFile() throws IOException
    {
        Path filepath = getPath();
        if (!Files.exists(filepath))
        {
            return;
        }

        JsonObject object = parseJson(IOUtils.readFile(filepath), JsonObject.class);
        if (object == null)
        {
            return;
        }

        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            T container = registry.get(entry.getKey());
            if (container == null)
            {
                continue;
            }

            JsonObject sObject = entry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> element : sObject.entrySet())
            {
                Setting<?> setting = container.getSetting(element.getKey());
                if (setting == null)
                {
                    continue;
                }

                try
                {
                    setting.fromJson(element.getValue());
                }
                catch (Exception e)
                {
                    Loader.error("Failed to set value from json", e);
                }
            }
        }
    }

    public List<Setting<?>> getSettings(SettingContainer container)
    {
        return container.getSettings()
                .stream()
                .filter(setting -> !setting.isTransient())
                .toList();
    }
}
