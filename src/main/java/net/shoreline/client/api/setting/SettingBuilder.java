package net.shoreline.client.api.setting;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SettingBuilder<T>
{
    private SettingFactory<T> factory;

    protected final String name;

    protected String description;
    private String[] nameAliases;
    private T defaultValue;
    private Supplier<Boolean> visible;
    private Consumer<T> observer;

    public SettingBuilder(String name)
    {
        this.name = name;
        this.description = "No description found!";
    }

    public SettingBuilder<T> setObserver(Consumer<T> observer)
    {
        this.observer = observer;
        return this;
    }

    public SettingBuilder<T> setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public SettingBuilder<T> setNameAliases(String... aliases)
    {
        this.nameAliases = aliases;
        return this;
    }

    public SettingBuilder<T> setDefaultValue(T value)
    {
        this.defaultValue = value;
        this.factory = new SettingFactory<>(value);
        return this;
    }

    public SettingBuilder<T> setVisible(Supplier<Boolean> visible)
    {
        this.visible = visible;
        return this;
    }

    public Setting<T> build()
    {
        return buildInternal(factory.create(name, description));
    }

    protected Setting<T> buildInternal(Setting<T> setting)
    {
        if (factory == null)
        {
            throw new IllegalStateException("Setting has no default value!");
        }

        if (nameAliases != null)
        {
            setting.setAliases(nameAliases);
        }

        if (observer != null)
        {
            setting.addObserver(observer);
        }

        if (defaultValue != null)
        {
            setting.setValue(defaultValue);
            setting.setDefaultValue(defaultValue);
        }

        setting.setVisible(visible);
        return setting;
    }
}
