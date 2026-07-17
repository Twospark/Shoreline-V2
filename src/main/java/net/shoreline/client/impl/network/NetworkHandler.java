package net.shoreline.client.impl.network;

import net.minecraft.network.protocol.Packet;
import net.shoreline.client.api.common.Feature;
import net.shoreline.client.impl.Managers;

public class NetworkHandler extends Feature
{
    public NetworkHandler(String name)
    {
        super(name);
        Managers.NETWORK.registerHandler(this);
    }

    public NetworkHandler(String name, String[] nameAliases)
    {
        super(name, nameAliases);
        Managers.NETWORK.registerHandler(this);
    }

    public final void sendPacket(Packet<?> packet)
    {
        Managers.NETWORK.sendPacket(this, packet);
    }

    public final void sendQuietPacket(Packet<?> packet)
    {
        Managers.NETWORK.sendQuietPacket(this, packet);
    }

    public final void sendSequencedPacket(SequencedPacketCreator packet)
    {
        Managers.NETWORK.sendSequencedPacket(this, packet);
    }

    public boolean wasSentFromClient(Packet<?> packet)
    {
        return Managers.NETWORK.wasSentFromClient(packet);
    }
}
