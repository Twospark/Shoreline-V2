package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.shoreline.eventbus.Event;

@RequiredArgsConstructor
@Getter
public class RenderFloatingItemEvent extends Event
{
    private final ItemStack stack;
}
