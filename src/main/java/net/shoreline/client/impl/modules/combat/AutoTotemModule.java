package net.shoreline.client.impl.modules.combat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.EnumSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.inventory.InventoryUtil;
import net.shoreline.client.impl.modules.impl.InventorySwapModule;
import net.shoreline.client.impl.modules.impl.Priorities;
import net.shoreline.client.util.Formatter;
import net.shoreline.client.util.entity.DamageUtil;
import net.shoreline.eventbus.api.Subscribe;

public class AutoTotemModule extends InventorySwapModule
{
    public static AutoTotemModule INSTANCE;

    Setting<ItemMode> itemMode = new EnumSetting.Builder<ItemMode>("Mode")
            .setDescription("The item to hold in your offhand")
            .setDefaultValue(ItemMode.TOTEM).build();
    Setting<Float> health = new NumberSetting.Builder<Float>("Health")
            .setMin(0.0f).setMax(20.0f).setDefaultValue(16.0f)
            .setDescription("Min health before swapping to a totem").build();
    Setting<Boolean> lethal = new BooleanSetting.Builder("Lethal")
            .setDescription("Swaps if potential lethal damage")
            .setDefaultValue(true).build();
    Setting<Boolean> instant = new BooleanSetting.Builder("Instant")
            .setDescription("Instantly replaces after popping a totem")
            .setDefaultValue(false).build();
    Setting<Boolean> fastSwap = new BooleanSetting.Builder("FastSwap")
            .setDescription("Uses a faster swapping method")
            .setDefaultValue(false).build();
    Setting<Boolean> mainhandTotem = new BooleanSetting.Builder("MainhandTotem")
            .setDescription("Holds a totem in your mainhand")
            .setDefaultValue(false).build();
    Setting<Integer> hotbarTotemSlot = new NumberSetting.Builder<Integer>("HotbarTotemSlot")
            .setMin(0).setMax(8).setDefaultValue(5)
            .setVisible(() -> false)
            .setDescription("The mainhand totem slot").build();

    @Getter
    private boolean isTotemInOffHand, isTotemInMainHand;
    private boolean clearedTotem;

    public AutoTotemModule()
    {
        super("AutoTotem", new String[]{"Offhand"}, "Automatically puts a totem in your offhand", Category.COMBAT);
        INSTANCE = this;
    }

    @Override
    public String getDisplayInfo()
    {
        return String.valueOf(InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING));
    }

    @Subscribe(priority = Priorities.AUTO_TOTEM)
    public void onTick(TickEvent event)
    {
        if (checkNull() || !canSwapInventory())
        {
            return;
        }

        AbstractContainerMenu handler = mc.player.containerMenu;
        float playerHealth = DamageUtil.getHealth(mc.player);

        isTotemInMainHand = mainhandTotem.getValue() && playerHealth - DamageUtil.getCrystalDamage(mc.player) <= 2.0;
        if (isTotemInMainHand)
        {
            ItemStack stack = mc.player.getInventory().getItem(hotbarTotemSlot.getValue());
            if (stack.isEmpty() || stack.getItem() != Items.TOTEM_OF_UNDYING)
            {
                swapItemWithSlot(Items.TOTEM_OF_UNDYING, hotbarTotemSlot.getValue(), fastSwap.getValue());
            }

            Managers.INVENTORY.setSelectedSlot(hotbarTotemSlot.getValue());
        }

        double potentialDamage = 0.5;
        potentialDamage += DamageUtil.getFallDamage(mc.player, mc.player.fallDistance, 1.0f);
        if (lethal.getValue())
        {
            potentialDamage += DamageUtil.getCrystalDamage(mc.player);
        }

        isTotemInOffHand = playerHealth - potentialDamage <= health.getValue();
        if (!isTotemInOffHand && OffhandGappleModule.INSTANCE.isGappleInOffHand())
        {
            return;
        }

        Item offhandItem = mc.player.getOffhandItem().getItem();
        Item requiredItem = isTotemInOffHand ? Items.TOTEM_OF_UNDYING : itemMode.getValue().getItem();
        if (offhandItem.equals(requiredItem))
        {
            return;
        }

        int returnSlot = OffhandGappleModule.INSTANCE.getReturnSlot();
        if (offhandItem == Items.ENCHANTED_GOLDEN_APPLE && returnSlot != -1)
        {
            Managers.INVENTORY.pickupSlot(handler, InventoryUtil.OFFHAND_SLOT);
            Managers.INVENTORY.pickupSlot(handler, InventoryUtil.getPacketSlotIndex(handler, returnSlot));
            if (handler.getCarried().getItem().equals(requiredItem))
            {
                Managers.INVENTORY.pickupSlot(handler, InventoryUtil.OFFHAND_SLOT);
            }
            else
            {
                swapItemWithSlot(requiredItem, Inventory.SLOT_OFFHAND, fastSwap.getValue());
            }

            return;
        }

        swapItemWithSlot(requiredItem, Inventory.SLOT_OFFHAND, fastSwap.getValue());
    }

    @RequiredArgsConstructor
    @Getter
    public enum ItemMode
    {
        TOTEM(Items.TOTEM_OF_UNDYING),
        GAPPLE(Items.ENCHANTED_GOLDEN_APPLE),
        CRYSTAL(Items.GOLDEN_APPLE);

        private final Item item;
    }
}
