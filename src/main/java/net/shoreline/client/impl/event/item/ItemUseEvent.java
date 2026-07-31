package net.shoreline.client.impl.event.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.shoreline.eventbus.Event;

@RequiredArgsConstructor
@Getter
public class ItemUseEvent
{
    private final Item item;

    @Getter
    @Setter
    public static class Block extends Event
    {
        private ItemStack itemStack;
    }
}