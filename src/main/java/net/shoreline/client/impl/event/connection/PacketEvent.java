package net.shoreline.client.impl.event.connection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.shoreline.eventbus.Event;

@Getter
@RequiredArgsConstructor
public class PacketEvent<T extends Packet<? extends PacketListener>> extends Event
{
    private final T packet;

    public static class Send<T extends Packet<? extends PacketListener>>
            extends PacketEvent<T>
    {
        public Send(T packet)
        {
            super(packet);
        }
    }

    @Getter
    public static class Receive<T extends Packet<? extends PacketListener>>
            extends PacketEvent<T>
    {
        private final Connection connection;

        public Receive(T packet, Connection connection)
        {
            super(packet);
            this.connection = connection;
        }
    }

    public static class Post<T extends Packet<? extends PacketListener>>
            extends PacketEvent<T>
    {
        public Post(T packet)
        {
            super(packet);
        }
    }
}
