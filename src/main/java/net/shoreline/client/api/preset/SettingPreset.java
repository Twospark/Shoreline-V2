package net.shoreline.client.api.preset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.util.SettingContainer;
import net.shoreline.loader.Loader;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SettingPreset<T extends SettingContainer> extends AbstractPreset<T>
{
    public SettingPreset(String name, Collection<T> values) throws IOException
    {
        super(name, values);
    }

    @Override
    protected void apply(T container, JsonObject object)
    {
        for (Setting<?> setting : container.getSettings())
        {
            object.add(setting.getId(), setting.toJson());
        }
    }

    @Override
    protected void load(T container, JsonObject object)
    {
        for (Map.Entry<String, JsonElement> element : object.entrySet())
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
