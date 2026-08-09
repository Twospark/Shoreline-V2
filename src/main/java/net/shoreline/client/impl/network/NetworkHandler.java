package net.shoreline.client.impl.network;

import net.minecraft.client.multiplayer.prediction.PredictiveAction;
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

    public void sendPacket(Packet<?> packet)
    {
        Managers.NETWORK.send(this, packet);
    }

    public void sendPacketQuietly(Packet<?> packet)
    {
        Managers.NETWORK.sendQuietly(this, packet);
    }

    public void sendSequencedPacket(PredictiveAction predictiveAction)
    {
        Managers.NETWORK.sendSequenced(predictiveAction);
    }

    public boolean wasSentFromClient(Packet<?> packet)
    {
        return Managers.NETWORK.wasSentFromClient(packet);
    }
}
