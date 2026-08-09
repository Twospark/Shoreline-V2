package net.shoreline.client.api.gui.component;

import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.api.setting.impl.RegistrySetting;
import net.shoreline.client.api.setting.util.ListType;

public class RegistryComponent<T> extends ParentComponent
{
    public RegistryComponent(RegistrySetting<T> setting)
    {
        super(setting.getName(), setting.getVisible());
        EnumSetting<ListType> placeholder = (EnumSetting<ListType>) new EnumSetting.Builder<ListType>("Type")
                .setDefaultValue(setting.getType())
                .setDescription("Only a placeholder so we can add a enum component").build();

        this.components.add(new EnumComponent<>(placeholder));
        this.components.add(new RegistrySelectionComponent<>(setting));
    }
}
