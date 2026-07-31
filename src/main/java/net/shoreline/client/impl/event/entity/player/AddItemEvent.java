package net.shoreline.client.impl.event.entity.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.shoreline.eventbus.Event;

@RequiredArgsConstructor
@Getter
public class AddItemEvent extends Event
{
    private final int slot;
    private final ItemStack stack;
}
