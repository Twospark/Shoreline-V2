package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Entity;
import net.shoreline.eventbus.Event;

@RequiredArgsConstructor
@Getter
public class RenderEntityEvent extends Event
{
    private final Entity entity;
}
