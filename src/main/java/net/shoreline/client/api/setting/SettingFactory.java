package net.shoreline.client.api.setting;

import lombok.RequiredArgsConstructor;
import net.shoreline.client.api.setting.impl.*;
import net.shoreline.client.api.setting.util.Bind;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class SettingFactory<T>
{
    private static final Map<Class<?>, BiFunction<String, String, Setting>> FACTORIES = new HashMap<>();

    static
    {
        FACTORIES.put(Boolean.class, BooleanSetting::new);
        FACTORIES.put(Color.class, ColorSetting::new);
        FACTORIES.put(Number.class, NumberSetting::new);
        FACTORIES.put(Void.class, SettingGroup::new);
        FACTORIES.put(String.class, StringSetting::new);
        FACTORIES.put(Enum.class, EnumSetting::new);
        FACTORIES.put(Bind.class, BindSetting::new);
    }

    private final T value;

    public Setting<T> create(final String name, final String description)
    {
        Setting<T> setting = FACTORIES.get(value.getClass())
                .apply(name, description);

        if (setting == null)
        {
            throw new IllegalArgumentException("Unsupported config type: " + value.getClass().getName());
        }

        return setting;
    }
}
