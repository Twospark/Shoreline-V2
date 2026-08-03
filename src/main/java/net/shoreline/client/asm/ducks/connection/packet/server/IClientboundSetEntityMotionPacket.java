package net.shoreline.client.asm.ducks.connection.packet.server;

import net.minecraft.world.phys.Vec3;

public interface IClientboundSetEntityMotionPacket
{
    void setMovement(Vec3 vec);

}
