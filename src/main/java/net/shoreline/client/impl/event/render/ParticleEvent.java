package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.particles.ParticleType;
import net.shoreline.eventbus.Event;

@Getter
@RequiredArgsConstructor
public class ParticleEvent extends Event
{
    private final ParticleType<?> type;
}
