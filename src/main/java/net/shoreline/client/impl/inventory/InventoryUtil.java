package net.shoreline.client.impl.inventory;

import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.modules.client.InventoryModule;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class InventoryUtil implements Globals
{
    public static final int INVALID_SLOT = -1;
    public static final int OFFHAND_SLOT = 45;

    public boolean isInInventoryScreen()
    {
        return mc.screen instanceof ContainerScreen
                || mc.screen instanceof ShulkerBoxScreen
                || mc.screen instanceof InventoryScreen;
    }

    public ItemSlot getItemSlot(Function<ItemStack, Boolean> stackFilter)
    {
        return getItemSlot(stackFilter, InventoryModule.INSTANCE.getSilentSwapType());
    }

    public ItemSlot getItemSlot(Function<ItemStack, Boolean> stackFilter, SilentSwapType type)
    {
        Inventory inv = mc.player.getInventory();

        ItemStack itemStack = null;
        int bestSlot = INVALID_SLOT;
        int bestScore = -1;

        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty() || !stackFilter.apply(stack))
            {
                continue;
            }

            int rank = getMaterialRank(stack);
            if (rank > bestScore)
            {
                bestScore = rank;
                if (type == SilentSwapType.INVENTORY || i < Inventory.getSelectionSize())
                {
                    bestSlot = i;
                    itemStack = stack;
                }
            }
        }

        return new ItemSlot(bestSlot, itemStack);
    }

    public ItemSlot getItem(Item item)
    {
        return getItem(item, InventoryModule.INSTANCE.getSilentSwapType());
    }

    public ItemSlot getItem(Item item, SilentSwapType type)
    {
        return type == SilentSwapType.INVENTORY ? getInventorySlot(item) : getHotbarItem(item);
    }

    public int getItemSlot(Item item)
    {
        return getItemSlot(item, InventoryModule.INSTANCE.getSilentSwapType());
    }

    public int getItemSlot(Item item, SilentSwapType type)
    {
        return type == SilentSwapType.INVENTORY ? getInventorySlot(item).getSlot() : getHotbarItem(item).getSlot();
    }

    public ItemSlot getInventorySlot(Item item)
    {
        Inventory inventory = mc.player.getInventory();
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem().equals(item))
            {
                return new ItemSlot(i, stack);
            }
        }

        return new ItemSlot(INVALID_SLOT, ItemStack.EMPTY);
    }

    public ItemSlot getHotbarItem(Predicate<ItemStack> predicate)
    {
        Inventory inventory = mc.player.getInventory();
        for (int i = 0; i < Inventory.getSelectionSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (predicate.test(stack))
            {
                return new ItemSlot(i, stack);
            }
        }

        return new ItemSlot(INVALID_SLOT, ItemStack.EMPTY);
    }

    public ItemSlot getHotbarItem(Item item)
    {
        Inventory inventory = mc.player.getInventory();
        for (int i = 0; i < Inventory.getSelectionSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem().equals(item))
            {
                return new ItemSlot(i, stack);
            }
        }

        return new ItemSlot(INVALID_SLOT, ItemStack.EMPTY);
    }

    public int find(Predicate<ItemStack> tester)
    {
        Inventory inventory = mc.player.getInventory();
        for (int i = 0; i < 45; i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (tester.test(stack))
            {
                return i;
            }
        }

        return -1;
    }

    public int getItemCount(Item item)
    {
        int count = 0;
        Inventory inventory = mc.player.getInventory();
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem().equals(item))
            {
                count += stack.getCount();
            }
        }

        ItemStack offhand = inventory.getItem(Inventory.SLOT_OFFHAND);
        if (offhand.getItem().equals(item))
        {
            count += offhand.getCount();
        }

        return count;
    }

    public static int getPacketSlotIndex(AbstractContainerMenu handler, int slot)
    {
        if (handler instanceof InventoryMenu)
        {
            if (slot == Inventory.SLOT_OFFHAND)
            {
                return Inventory.SLOT_OFFHAND;
            }

            if (slot > 100)
            {
                return 108 - slot;
            }

            return slot < Inventory.SELECTION_SIZE ? slot + Inventory.INVENTORY_SIZE : slot;
        }

        final List<Slot> slots = handler.slots;
        for (int id = 0; id < slots.size(); id++)
        {
            Slot s = slots.get(id);
            if (s.getContainerSlot() == slot)
            {
                return id;
            }
        }

        return slot;
    }

    private int getMaterialRank(ItemStack stack)
    {
        String key = stack.getItem().getDescriptionId();
        if (key.contains("netherite")) return 600;
        if (key.contains("diamond"))   return 500;
        if (key.contains("iron"))      return 400;
        if (key.contains("gold"))      return 300;
        if (key.contains("stone"))     return 200;
        if (key.contains("wood"))      return 100;

        return 0;
    }
}
