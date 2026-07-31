package net.shoreline.client.impl.inventory;

import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Getter
public class HotbarCache
{
    private final ItemStack[] hotbarItems;

    public HotbarCache(Inventory playerInventory)
    {
        this(playerInventory, false);
    }

    public HotbarCache(Inventory playerInventory, boolean copyStack)
    {
        this.hotbarItems = new ItemStack[Inventory.getSelectionSize()];
        for (int i = 0; i < hotbarItems.length; i++)
        {
            ItemStack stack = playerInventory.getItem(i);
            hotbarItems[i] = copyStack ? stack.copy() : stack;
        }
    }

    public ItemStack getStack(int slot)
    {
        return hotbarItems[slot];
    }
}