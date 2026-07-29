package net.shoreline.client.api.setting.util;

import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.impl.network.NetworkHandler;
import net.shoreline.loader.Loader;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.SequencedCollection;

public class SettingContainer extends NetworkHandler
{
    private final LinkedHashMap<String, Setting<?>> settings = new LinkedHashMap<>();

    public SettingContainer(String name, String[] nameAliases)
    {
        super(name, nameAliases);
    }

    public void reflectSettings()
    {
        for (Field field : getClass().getDeclaredFields())
        {
            if (Setting.class.isAssignableFrom(field.getType()))
            {
                try
                {
                    field.setAccessible(true);
                    Setting<?> config = (Setting<?>) field.get(this);
                    if (config == null)
                    {
                        continue;
                    }

                    register(config);
                }
                catch (IllegalArgumentException | IllegalAccessException e)
                {
                    Loader.error("Failed to build config from field {}!", field.getName());
                    e.printStackTrace();
                }
            }
        }
    }


    protected void register(Setting<?> config)
    {
        settings.put(config.getId(), config);
    }

    protected void register(Setting<?>... config)
    {
        Arrays.stream(config).forEach(this::register);
    }

    protected void unregister(Setting<?> config)
    {
        settings.remove(config.getId());
    }

    public Setting<?> getSetting(String id)
    {
        return settings.get(id.toLowerCase());
    }

    public SequencedCollection<Setting<?>> getSettings()
    {
        return settings.sequencedValues();
    }
}
