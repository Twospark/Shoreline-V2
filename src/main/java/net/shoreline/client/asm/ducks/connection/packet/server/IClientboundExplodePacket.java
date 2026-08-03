package net.shoreline.client.asm.ducks.connection.packet.server;

import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public interface IClientboundExplodePacket
{
    void setPlayerKnockback(Optional<Vec3> optional);
}
