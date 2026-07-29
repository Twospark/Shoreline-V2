package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.component.ParentComponent;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

@Getter
@Setter
public class SettingGroup extends Setting<Void> implements Iterable<Setting<?>>
{
    private LinkedHashMap<String, Setting<?>> settings;

    public SettingGroup(String name, String description)
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
        // NOP
    }

    @Override
    public JsonElement toJson()
    {
        return null;
    }

    @Override
    public boolean isTransient()
    {
        return true;
    }

    @Override
    public GuiComponent getComponent()
    {
        ParentComponent component = new ParentComponent(getName(), getVisible());
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

    public static class Builder extends SettingBuilder<Void>
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

        public SettingGroup build()
        {
            setDefaultValue(null);
            SettingGroup group = (SettingGroup) super.build();
            group.setSettings(settings);

            settings.values().forEach(c -> c.setGrouped(true));
            return group;
        }
    }
}
