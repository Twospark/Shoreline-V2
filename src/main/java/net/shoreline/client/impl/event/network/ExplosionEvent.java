package net.shoreline.client.impl.event.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.phys.Vec3;
import net.shoreline.eventbus.Event;

@AllArgsConstructor
@Getter
@Setter
public class ExplosionEvent extends Event
{
    private final Vec3 center;
    private Vec3 velocity;
}
