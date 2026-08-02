package net.shoreline.client.impl.modules.hud;

import net.minecraft.ChatFormatting;
import net.shoreline.client.api.element.dynamic.DynamicElement;
import net.shoreline.client.api.element.dynamic.DynamicEntry;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.impl.Managers;

public class ModulesElement extends DynamicElement
{
    Setting<Boolean> onlyBound = new BooleanSetting.Builder("Bound")
            .setDescription("Only shows modules who have a bind")
            .setDefaultValue(false).build();
    Setting<Boolean> showInfo = new BooleanSetting.Builder("ShowInfo")
            .setDescription("Shows extra module info")
            .setDefaultValue(true).build();

    public ModulesElement()
    {
        super("Modules", "Shows your enabled modules", 200, 2);
    }

    @Override
    public void loadEntries()
    {
        for (Module module : Managers.MODULES.getRegistry().getCollection())
        {
            if (module instanceof Toggleable toggleable)
            {
                getEntries().add(new DynamicEntry(
                        this,
                        () -> getFullName(toggleable),
                        () -> toggleable.isEnabled()
                                && !toggleable.isHidden()
                                && (!onlyBound.getValue() || toggleable.getBind().getKey() != -1)));
            }
        }
    }

    public String getFullName(Toggleable module)
    {
        if (!showInfo.getValue())
        {
            return module.getName();
        }

        return module.getName() + (module.getDisplayInfo() == null
                ? ""
                : ChatFormatting.GRAY + " ["
                  + ChatFormatting.WHITE + module.getDisplayInfo()
                  + ChatFormatting.GRAY + "]");
    }
}
