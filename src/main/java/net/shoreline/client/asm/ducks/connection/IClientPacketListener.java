package net.shoreline.client.asm.ducks.connection;

import net.minecraft.network.protocol.Packet;

public interface IClientPacketListener
{
    void shoreline$sendQuietPacket(Packet<?> packet);

}
