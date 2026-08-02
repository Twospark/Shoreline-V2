package net.shoreline.client.impl.modules.impl;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.inventory.InventoryUtil;

public class InventorySwapModule extends Toggleable
{
    public InventorySwapModule(String name, String description, Category category)
    {
        super(name, description, category);
    }

    public InventorySwapModule(String name, String[] nameAliases, String description, Category category)
    {
        super(name, nameAliases, description, category);
    }

    protected int swapItemWithSlot(Item item, int slot, boolean altSwap)
    {
        Inventory playerInventory = mc.player.getInventory();
        AbstractContainerMenu handler = mc.player.containerMenu;
        for (int i = 0; i < Inventory.INVENTORY_SIZE; ++i)
        {
            ItemStack stack = playerInventory.getItem(i);
            if (!stack.getItem().equals(item))
            {
                continue;
            }

            int slot1 = InventoryUtil.getPacketSlotIndex(handler, slot);
            if (altSwap)
            {
                Managers.INVENTORY.swap(i, slot1);
            }
            else
            {
                Managers.INVENTORY.clickSwap(i, slot1, item);
            }

            return i;
        }

        return -1;
    }

    protected boolean canSwapInventory()
    {
        return mc.screen == null || mc.screen instanceof InventoryScreen || mc.screen instanceof ChatScreen;
    }
}