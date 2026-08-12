package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.core.particles.ParticleOptions;
import net.shoreline.eventbus.Event;

@RequiredArgsConstructor
@Getter
@Setter
public class EmitParticleEvent extends Event
{
    private final ParticleOptions effect;
    private int maxTicks;
    private int maxCount;
}