package net.shoreline.client.impl.modules.combat;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.inventory.InventoryUtil;
import net.shoreline.client.impl.modules.impl.InventorySwapModule;
import net.shoreline.client.impl.modules.impl.Priorities;
import net.shoreline.eventbus.api.Subscribe;

@Getter
public class OffhandGappleModule extends InventorySwapModule
{
    public static OffhandGappleModule INSTANCE;

    Setting<Boolean> fastSwap = new BooleanSetting.Builder("FastSwap")
            .setDescription("Uses a faster swapping method")
            .setDefaultValue(false).build();
    Setting<Boolean> swords = new BooleanSetting.Builder("Swords")
            .setDescription("Allows gapples in offhand when holding a sword")
            .setDefaultValue(true).build();
    Setting<Boolean> tools = new BooleanSetting.Builder("Tools")
            .setDescription("Allows gapples in offhand when holding a tool")
            .setDefaultValue(true).build();
    Setting<Boolean> totem = new BooleanSetting.Builder("Totems")
            .setDescription("Allows gapples in offhand when holding a totem")
            .setDefaultValue(true).build();

    private boolean isGappleInOffHand;

    @Setter
    private int returnSlot = InventoryUtil.INVALID_SLOT;

    public OffhandGappleModule()
    {
        super("OffhandGapple", "Swaps golden apples into your offhand", Category.COMBAT);
        INSTANCE = this;
    }

    @Override
    public String getDisplayInfo()
    {
        return String.valueOf(InventoryUtil.getItemCount(Items.ENCHANTED_GOLDEN_APPLE));
    }

    @Subscribe(priority = Priorities.OFFHAND)
    public void onTick(TickEvent event)
    {
        if (checkNull() || AutoTotemModule.INSTANCE.isTotemInOffHand())
        {
            return;
        }

        ItemStack stack = mc.player.getMainHandItem();
        isGappleInOffHand = canEatWhileHolding(stack.getItem()) && mc.options.keyUse.isDown();

        if (!isGappleInOffHand || mc.player.getOffhandItem().getItem().equals(Items.ENCHANTED_GOLDEN_APPLE))
        {
            return;
        }

        returnSlot = swapItemWithSlot(Items.ENCHANTED_GOLDEN_APPLE, Inventory.SLOT_OFFHAND, fastSwap.getValue());
    }

    private boolean canEatWhileHolding(Item item)
    {
        ItemStack defaultStack = item.getDefaultInstance();
        return swords.getValue() && defaultStack.is(ItemTags.SWORDS)
                || tools.getValue() && isTool(defaultStack)
                || totem.getValue() && item == Items.TOTEM_OF_UNDYING;
    }

    private boolean isTool(ItemStack stack)
    {
        return stack.is(ItemTags.SHOVELS) || stack.is(ItemTags.AXES) || stack.is(ItemTags.PICKAXES);
    }
}
