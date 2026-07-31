package net.shoreline.client.impl.event.entity.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.phys.Vec3;
import net.shoreline.eventbus.Event;

@AllArgsConstructor
@Getter
@Setter
public class TravelEvent extends Event
{
    private Vec3 movementInput;

    public static class Pre extends TravelEvent
    {
        public Pre(Vec3 movementInput)
        {
            super(movementInput);
        }
    }

    public static class Post extends TravelEvent
    {
        public Post(Vec3 movementInput)
        {
            super(movementInput);
        }
    }
}
