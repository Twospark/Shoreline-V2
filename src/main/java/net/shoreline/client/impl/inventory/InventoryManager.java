package net.shoreline.client.impl.inventory;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.impl.event.LevelEvent;
import net.shoreline.client.impl.event.connection.PacketEvent;
import net.shoreline.client.impl.event.entity.player.AddItemEvent;
import net.shoreline.client.impl.event.item.ItemUseEvent;
import net.shoreline.client.impl.event.network.SetHandEvent;
import net.shoreline.client.impl.modules.client.InventoryModule;
import net.shoreline.client.impl.network.NetworkHandler;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InventoryManager extends NetworkHandler
{
    public static InventoryModule inventoryConfig = InventoryModule.INSTANCE;

    private final SwapData.Mutable current = new SwapData.Mutable();
    private final SwapData.Mutable multitick = new SwapData.Mutable();

    private final List<SwapData> trackedHotbar    = new CopyOnWriteArrayList<>();
    private final List<SwapData> trackedInventory = new CopyOnWriteArrayList<>();

    private boolean usingItem;
    private int serverSlot;

    public InventoryManager()
    {
        super("Inventory");
        EventBus.getInstance().subscribe(this);
    }

    @Subscribe
    public void onDisconnect(LevelEvent.Disconnect event)
    {
        trackedHotbar.clear();
        trackedInventory.clear();
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive<?> event)
    {
        if (checkNull())
        {
            return;
        }

        if (event.getPacket() instanceof ClientboundSetHeldSlotPacket(int slot))
        {
            serverSlot = slot;
            return;
        }

        if (event.getPacket() instanceof ServerboundPlayerActionPacket packet
                && packet.getAction() == ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM
                && multitick.isSwapped() && usingItem)
        {
            int slot = multitick.getSlotTo();
            if (serverSlot != slot)
            {
                sendPacket(new ServerboundSetCarriedItemPacket(slot));
            }

            usingItem = false;
            return;
        }

        if (event.getPacket() instanceof ClientboundContainerSetSlotPacket packet
                && InventoryUtil.isInInventoryScreen())
        {
            int slot = packet.getSlot();
            if (packet.getItem().isEmpty() || !Inventory.isHotbarSlot(slot))
            {
                return;
            }

            for (SwapData data : trackedHotbar)
            {
                if (data.getSwapTime() > 500L)
                {
                    trackedHotbar.remove(data);
                    continue;
                }

                if (data.getSlotTo() != slot && data.getSlotFrom() != slot)
                {
                    continue;
                }

                ItemStack preStack = data.getPreHotbar().getStack(slot);
                if (!ItemStack.isSameItem(preStack, packet.getItem()))
                {
                    event.setCanceled(true);
                    return;
                }
            }

            for (SwapData data : trackedInventory)
            {
                if (data.getSlotTo() != slot && data.getSlotFrom() != slot)
                {
                    continue;
                }

                ItemStack preStack = data.getPreHotbar().getStack(slot);
                if (!ItemStack.isSameItem(preStack, packet.getItem()))
                {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send<?> event)
    {
        if (event.getPacket() instanceof ServerboundSetCarriedItemPacket packet)
        {
            int packetSlot = packet.getSlot();
            if (serverSlot == packetSlot)
            {
                event.setCanceled(true);
                return;
            }

            serverSlot = packetSlot;
        }
    }

    @Subscribe
    public void onSetHand(SetHandEvent event)
    {
        if (multitick.isSwapped() && isSilentSwapping())
        {
            sendPacket(new ServerboundSetCarriedItemPacket(mc.player.getInventory().getSelectedSlot()));
            usingItem = true;
        }
    }

    @Subscribe
    public void onAddItem(AddItemEvent event)
    {
        if (mc.isSingleplayer() || trackedInventory.isEmpty() || !Inventory.isHotbarSlot(event.getSlot()))
        {
            return;
        }

        for (SwapData data : trackedInventory)
        {
            if (data.getSwapTime() > 500L)
            {
                trackedInventory.remove(data);
                continue;
            }

            if (data.getSlotTo() == event.getSlot() || data.getSlotFrom() == event.getSlot())
            {
                event.setCanceled(true);
                return;
            }
        }
    }

    @Subscribe
    public void onItemUseOnBlock(ItemUseEvent.Block event)
    {
        if (mc.player != null && current.isSwapped())
        {
            event.setCanceled(true);
            event.setItemStack(current.getItemStack(mc.player.getInventory()));
        }
    }

    public boolean isSilentSwapping()
    {
        return mc.player != null && mc.player.getInventory().getSelectedSlot() != serverSlot;
    }

    public void setSelectedSlot(int slot)
    {
        if (slot != mc.player.getInventory().getSelectedSlot())
        {
            mc.player.getInventory().setSelectedSlot(slot);
        }

        if (slot != serverSlot)
        {
            sendPacket(new ServerboundSetCarriedItemPacket(slot));
        }
    }

    public boolean startSwap(int itemSlot)
    {
        return startSwap(itemSlot, inventoryConfig.getSilentSwapType());
    }

    public boolean startSwap(int itemSlot, SilentSwapType swapType)
    {
        Inventory playerInventory = mc.player.getInventory();
        if (swapType == SilentSwapType.HOTBAR && !Inventory.isHotbarSlot(itemSlot))
        {
            return false;
        }

        if (playerInventory.getSelectedSlot() == itemSlot || current.isSwapped())
        {
            return true;
        }

        HotbarCache swapCache = new HotbarCache(playerInventory, true);

        int fromSlot = multitick.isSwapped() && !mc.player.isUsingItem()
                ? multitick.getSlotTo()
                : playerInventory.getSelectedSlot();

        current.setSwapped(true);
        current.setSlotTo(itemSlot);
        current.setSlotFrom(fromSlot);

        SwapData data = new SwapData(swapCache, itemSlot, fromSlot);
        switch (swapType)
        {
            case HOTBAR ->
            {
                sendPacket(new ServerboundSetCarriedItemPacket(itemSlot));
                trackedHotbar.add(data);
            }
            case INVENTORY ->
            {
                internalSwapSlot(itemSlot, fromSlot);
                trackedInventory.add(data);
            }
        }

        return true;
    }

    public void endSwap()
    {
        endSwap(inventoryConfig.getSilentSwapType());
    }

    public void endSwap(SilentSwapType swapType)
    {
        Inventory playerInventory = mc.player.getInventory();
        if (!current.isSwapped())
        {
            return;
        }

        switch (swapType)
        {
            case HOTBAR ->
            {
                if (isSilentSwapping())
                {
                    int returnSlot = multitick.isSwapped() && !mc.player.isUsingItem() ? multitick.getSlotTo() : playerInventory.getSelectedSlot();
                    sendPacket(new ServerboundSetCarriedItemPacket(returnSlot));
                }
            }

            case INVENTORY -> internalSwapSlot(current.getSlotTo(), current.getSlotFrom());
        }

        current.reset();
    }

    public boolean startMultitickSwap(int itemSlot)
    {
        if (current.isSwapped() || usingItem || !Inventory.isHotbarSlot(itemSlot))
        {
            return false;
        }

        multitick.setSwapped(true);
        multitick.setSlotTo(itemSlot);
        if (serverSlot != itemSlot)
        {
            sendPacket(new ServerboundSetCarriedItemPacket(itemSlot));
        }

        return true;
    }

    public void endMultitickSwap()
    {
        if (!multitick.isSwapped())
        {
            return;
        }

        usingItem = false;
        if (isSilentSwapping())
        {
            sendPacket(new ServerboundSetCarriedItemPacket(mc.player.getInventory().getSelectedSlot()));
        }

        multitick.reset();
    }

    public void clickSwap(int fromSlot, int toSlot, Item item)
    {
        AbstractContainerMenu handler = mc.player.containerMenu;
        int slot = InventoryUtil.getPacketSlotIndex(handler, fromSlot);
        if (!handler.getCarried().getItem().equals(item))
        {
            pickupSlot(handler, slot);
        }

        if (handler.getCarried().getItem().equals(item))
        {
            pickupSlot(handler, toSlot);
        }

        if (!handler.getCarried().isEmpty())
        {
            pickupSlot(handler, slot);
        }
    }

    public void swap(int fromSlot, int toSlot)
    {
        AbstractContainerMenu handler = mc.player.containerMenu;
        int slot = InventoryUtil.getPacketSlotIndex(handler, fromSlot);
        swapSlot(handler, slot, toSlot);
    }

    public void pickupSlot(AbstractContainerMenu handler, int slot)
    {
        mc.gameMode.handleContainerInput(handler.containerId, slot, 0, ContainerInput.PICKUP, mc.player);
    }

    public void swapSlot(AbstractContainerMenu handler, int slot1, int slot2)
    {
        mc.gameMode.handleContainerInput(handler.containerId, slot1, slot2, ContainerInput.SWAP, mc.player);
    }

    private void internalSwapSlot(int slot1, int slot2)
    {
        AbstractContainerMenu handler = mc.player.containerMenu;
        int slot = InventoryUtil.getPacketSlotIndex(handler, slot1);
        mc.gameMode.handleContainerInput(handler.containerId, slot, slot2, ContainerInput.SWAP, mc.player);
    }

    public boolean isHolding(Item item, InteractionHand hand)
    {
        ItemStack holdingStack = isSilentSwapping()
                && hand == InteractionHand.MAIN_HAND
                ? getServerStack()
                : mc.player.getItemInHand(hand);

        return holdingStack.getItem().equals(item);
    }

    public ItemStack getServerStack()
    {
        return mc.player.getInventory().getItem(serverSlot);
    }
}
