package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.state.BlockState;
import net.shoreline.eventbus.Event;

@RequiredArgsConstructor
@Getter
public class RenderBlockEvent extends Event
{
    private final BlockState state;
}
