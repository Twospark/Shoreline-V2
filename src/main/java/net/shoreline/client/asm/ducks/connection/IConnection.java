package net.shoreline.client.asm.ducks.connection;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.protocol.Packet;

public interface IConnection
{
    void shoreline$doSendPacket(Packet<?> packet, ChannelFutureListener listener, boolean flush);
}
