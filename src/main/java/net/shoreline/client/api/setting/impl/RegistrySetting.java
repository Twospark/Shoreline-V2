package net.shoreline.client.api.setting.impl;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.component.RegistryComponent;
import net.shoreline.client.api.gui.component.RegistrySelectionComponent;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.SettingBuilder;
import net.shoreline.client.api.setting.util.ListType;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class RegistrySetting<T> extends Setting<Collection<T>>
{
    private Registry<T> registry;
    private ListType type;

    public RegistrySetting(String name, String description)
    {
        super(name, description);
    }

    public void add(T element)
    {
        Collection<T> val = new LinkedHashSet<>(getValue());
        val.add(element);
        setValue(val);
    }

    public void remove(T element)
    {
        Collection<T> val = new LinkedHashSet<>(getValue());
        val.remove(element);
        setValue(val);
    }

    public boolean contains(T object)
    {
        return switch (type)
        {
            case WHITELIST -> getValue().contains(object);
            case BLACKLIST -> !getValue().contains(object);
        };
    }

    public void clear()
    {
        Collection<T> val = new LinkedHashSet<>();
        setValue(val);
    }

    @Override
    public void fromJson(JsonElement element)
    {
        Set<T> entries = new LinkedHashSet<>();
        String[] split = element.getAsString().split("-");
        for (String string : split)
        {
            if (string.isEmpty())
            {
                continue;
            }

            if (!string.startsWith("minecraft")) // list type
            {
                type = ListType.valueOf(string);
                continue;
            }

            T entry = registry.getValue(Identifier.parse(string));
            if (entry == null)
            {
                continue;
            }

            entries.add(entry);
        }

        setValue(entries);
    }

    @Override
    public JsonElement toJson()
    {
        StringBuilder builder = new StringBuilder();
        for (T entry : getValue())
        {
            int identifier = registry.getId(entry);
            if (identifier == -1)
            {
                continue;
            }

            String string = registry.getKey(entry).toString();
            if (string == null)
            {
                continue;
            }

            builder.append(string).append("-");
        }

        builder.append(type.toString());
        return parse("\"" + builder.toString().replace("\\", "\\\\") + "\"");
    }

    @Override
    public GuiComponent getComponent()
    {
        return new RegistryComponent<>(this);
    }

    public static class Builder<T> extends SettingBuilder<Collection<T>>
    {
        private Collection<T> values;
        private Registry<T> registry;
        private ListType listType = ListType.WHITELIST;

        public Builder(String name)
        {
            super(name);
            setDefaultValue(new LinkedHashSet<>());
        }

        public Builder<T> setRegistry(Registry<T> registry)
        {
            this.registry = registry;
            return this;
        }

        public Builder<T> setListType(ListType type)
        {
            this.listType = listType;
            return this;
        }

        @SafeVarargs
        public final Builder<T> setValues(T... values)
        {
            this.values = Set.of(values);
            return this;
        }

        @Override
        public RegistrySetting<T> build()
        {
            RegistrySetting<T> setting = (RegistrySetting<T>) super.buildInternal(new RegistrySetting<>(name, description));
            setting.setRegistry(registry);
            setting.setType(listType);
            setting.getValue().addAll(values);
            return setting;
        }
    }
}
