package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.Setter;
import net.shoreline.eventbus.Event;

@Getter
@Setter
public class RenderTickCounterEvent extends Event
{
    private float ticks;
}
