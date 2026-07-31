package net.shoreline.client.impl.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@RequiredArgsConstructor
@Getter
@Setter
public class ItemSlot
{
    private final int slot;
    private final ItemStack itemStack;

    public ItemSlot(Inventory inventory, int slot)
    {
        this.slot = slot;
        if (slot == InventoryUtil.INVALID_SLOT)
        {
            this.itemStack = ItemStack.EMPTY;
            return;
        }

        this.itemStack = inventory.getItem(slot);
    }

    public Item getItem()
    {
        return itemStack.getItem();
    }
}