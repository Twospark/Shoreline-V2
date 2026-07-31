package net.shoreline.client.impl.modules.client;

import lombok.Getter;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Concurrent;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.api.setting.impl.SettingGroup;
import net.shoreline.client.impl.inventory.SilentSwapType;

@Getter
public class InventoryModule extends Concurrent
{
    public static InventoryModule INSTANCE;

    Setting<SilentSwapType> silentSwap = new EnumSetting.Builder<SilentSwapType>("SilentSwap")
            .setDescription("The mode for silent swapping items")
            .setDefaultValue(SilentSwapType.HOTBAR).build();
    Setting<Boolean> assumeEnchanted = new BooleanSetting.Builder("AssumeEnchanted")
            .setDescription("Assumes that all enemies armor is max enchanted")
            .setDefaultValue(false).build();
    Setting<Boolean> dragQuickMove = new BooleanSetting.Builder("DragQuickMove")
            .setDescription("Allows you to drag quick move items in the inventory")
            .setDefaultValue(false).build();

    Setting<Boolean> mapTooltips = new BooleanSetting.Builder("Maps")
            .setDescription("Shows contents of maps in the inventory screen")
            .setDefaultValue(false).build();
    Setting<Boolean> shulkerTooltips = new BooleanSetting.Builder("Shulkers")
            .setDescription("Shows contents of shulkers in the inventory screen")
            .setDefaultValue(false).build();
    Setting<Void> tooltipsConfig = new SettingGroup.Builder("Tooltips")
            .addAll(mapTooltips, shulkerTooltips)
            .setDescription("Shows extra tooltips in the inventory screen").build();

    public InventoryModule()
    {
        super("Inventory", "Manages Shorelines internal InventoryManager", Category.CLIENT);
        INSTANCE = this;
    }

    public SilentSwapType getSilentSwapType()
    {
        return silentSwap.getValue();
    }
}
