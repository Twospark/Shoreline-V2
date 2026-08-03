package net.shoreline.client.asm.mixins.connection.packet.server;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.asm.ducks.connection.packet.server.IClientboundSetEntityMotionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientboundSetEntityMotionPacket.class)
public abstract class MixinClientboundSetEntityMotionPacket implements IClientboundSetEntityMotionPacket
{
    @Override
    @Accessor(value = "movement")
    public abstract void setMovement(Vec3 vec);
}
