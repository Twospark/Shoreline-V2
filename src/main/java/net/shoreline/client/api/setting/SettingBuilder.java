package net.shoreline.client.api.setting;

import java.util.function.Supplier;

public abstract class SettingBuilder<T>
{
    private SettingFactory<T> factory;

    protected final String name;

    protected String description;
    private String[] nameAliases;
    private T defaultValue;
    private Supplier<Boolean> visible;

    public SettingBuilder(String name)
    {
        this.name = name;
        this.description = "No description found!";
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
        return buildWithoutFactory(factory.create(name, description));
    }

    public Setting<T> buildWithoutFactory(Setting<T> config)
    {
        if (factory == null)
        {
            throw new IllegalStateException("Config has no default value!");
        }

        if (nameAliases != null)
        {
            config.setAliases(nameAliases);
        }

        if (defaultValue != null)
        {
            config.setValue(defaultValue);
            config.setDefaultValue(defaultValue);
        }

        config.setVisible(visible);
        return config;
    }
}
