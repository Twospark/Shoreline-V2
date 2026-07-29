package net.shoreline.client.asm.mixins.connection;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.shoreline.client.asm.ducks.connection.IClientPacketListener;
import net.shoreline.client.asm.ducks.connection.IConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener implements IClientPacketListener
{
    @Shadow
    public abstract Connection getConnection();

    @Override
    public void shoreline$sendQuietPacket(Packet<?> packet)
    {
        ((IConnection) getConnection()).shoreline$doSendPacket(packet, null, true);
    }
}
