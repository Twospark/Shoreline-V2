package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.component.ToggleableGroupComponent;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

@Setter
@Getter
public class ToggleableSettingGroup extends Setting<Boolean>
        implements Iterable<Setting<?>>
{
    private LinkedHashMap<String, Setting<?>> settings;

    public ToggleableSettingGroup(String name, String description)
    {
        super(name, description);
    }

    @Override
    public Iterator<Setting<?>> iterator()
    {
        return settings.sequencedValues().iterator();
    }

    @Override
    public void fromJson(JsonElement element)
    {
        setValue(element.getAsBoolean());
    }

    @Override
    public JsonElement toJson()
    {
        return parse(getValue().toString());
    }

    @Override
    public GuiComponent getComponent()
    {
        ToggleableGroupComponent component = new ToggleableGroupComponent(this);
        for (Setting<?> setting : settings.values())
        {
            GuiComponent sComponent = setting.getComponent();
            if (sComponent == null)
            {
                continue;
            }

            component.getComponents().add(sComponent);
        }

        return component;
    }

    public static class Builder extends SettingBuilder<Boolean>
    {
        private final LinkedHashMap<String, Setting<?>> settings = new LinkedHashMap<>();

        public Builder(String name)
        {
            super(name);
        }

        public Builder add(Setting<?> config)
        {
            settings.put(config.getName(), config);
            return this;
        }

        public Builder addAll(Setting<?>... configs1)
        {
            Arrays.stream(configs1).forEach(c -> settings.put(c.getName(), c));
            return this;
        }

        public ToggleableSettingGroup build()
        {
            ToggleableSettingGroup group = (ToggleableSettingGroup)
                    buildInternal(new ToggleableSettingGroup(name, description));

            group.setSettings(settings);

            settings.values().forEach(c -> c.setGrouped(true));
            return group;
        }
    }
}
