package net.shoreline.client.impl.event.entity.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.shoreline.eventbus.Event;

public class JumpEvent
{
    public static class Pre extends Event {}

    public static class Post {}

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Yaw extends Event
    {
        private float yaw;
    }
}
