package net.shoreline.client.asm.mixins.connection.packet.server;

import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.asm.ducks.connection.packet.server.IClientboundExplodePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(ClientboundExplodePacket.class)
public abstract class MixinClientboundExplodePacket implements IClientboundExplodePacket
{
    @Override
    @Accessor(value = "playerKnockback")
    public abstract void setPlayerKnockback(Optional<Vec3> optional);
}
