package net.shoreline.client.impl.event.network;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import net.shoreline.eventbus.Event;

public class MovementPacketsEvent extends Event
{
    public static class Update extends MovementPacketsEvent {}

    @Getter
    @Setter
    public static class Send extends MovementPacketsEvent
    {
        private Packet<?> packet;

        public Send(Packet<?> packet)
        {
            this.packet = packet;
        }
    }
}