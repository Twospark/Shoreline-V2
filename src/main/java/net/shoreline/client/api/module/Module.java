package net.shoreline.client.api.module;

import net.shoreline.client.api.setting.util.SettingContainer;

public class Module extends SettingContainer
{
    private final String description;
    private final Category category;

    public Module(String name,
                  String description,
                  Category category)
    {
        this(name, new String[0], description, category);
    }

    public Module(String name,
                  String[] nameAliases,
                  String description,
                  Category category)
    {
        super(name, nameAliases);
        this.description = description;
        this.category = category;
    }
}
