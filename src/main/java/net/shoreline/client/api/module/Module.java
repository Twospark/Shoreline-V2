package net.shoreline.client.api.module;

import lombok.Getter;
import net.shoreline.client.api.gui.api.Displayable;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.component.ModuleComponent;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.util.SettingContainer;

@Getter
public class Module extends SettingContainer implements Displayable
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
        reflectSettings();
    }

    @Override
    public GuiComponent getComponent()
    {
        ModuleComponent component = new ModuleComponent(this);
        for (Setting<?> setting : getSettings())
        {
            GuiComponent sComponent = setting.getComponent();
            if (sComponent == null || setting.isGrouped())
            {
                continue;
            }

            component.getComponents().add(sComponent);
        }

        return component;
    }

    public String getDisplayInfo()
    {
        return null;
    }
}
