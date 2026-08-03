package net.shoreline.client.asm.mixins.connection;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.asm.ducks.connection.IClientPacketListener;
import net.shoreline.client.asm.ducks.connection.IConnection;
import net.shoreline.client.impl.event.network.ExplosionEvent;
import net.shoreline.client.impl.event.network.RotationUpdateEvent;
import net.shoreline.client.impl.event.render.AddEntityEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener implements IClientPacketListener, Globals
{
    @Shadow
    public abstract Connection getConnection();

    @Override
    public void shoreline$sendQuietPacket(Packet<?> packet)
    {
        ((IConnection) getConnection()).shoreline$doSendPacket(packet, null, true);
    }

    @Inject(method = "handleRotatePlayer", at = @At(value = "HEAD"))
    private void handleRotatePlayerHook(ClientboundPlayerRotationPacket packet,
                                        CallbackInfo info)
    {
        RotationUpdateEvent.Pre event = new RotationUpdateEvent.Pre();
        EventBus.getInstance().post(event);
    }

    @Inject(
            method = "handleRotatePlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0))
    private void handleRotatePlayerPacketHook(ClientboundPlayerRotationPacket packet,
                                              CallbackInfo info)
    {
        RotationUpdateEvent.PrePacket event = new RotationUpdateEvent.PrePacket();
        EventBus.getInstance().post(event);
    }

    @Inject(method = "handleRotatePlayer", at = @At(value = "TAIL"))
    private void handleRotatePlayerHook_Tail(ClientboundPlayerRotationPacket packet,
                                             CallbackInfo info)
    {
        RotationUpdateEvent event = new RotationUpdateEvent(mc.player.getYRot(), mc.player.getXRot());
        EventBus.getInstance().post(event);
    }

    @Inject(
            method = "handleExplosion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;" +
                            "addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
            shift = At.Shift.AFTER),
            cancellable = true)
    private void handleExplosionHook(ClientboundExplodePacket packet, CallbackInfo info)
    {
        ExplosionEvent event = new ExplosionEvent(packet.center(), packet.playerKnockback().isPresent()
                ? packet.playerKnockback().get()
                : Vec3.ZERO);

        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
            Vec3 newVelo = event.getVelocity();
            if (newVelo != null)
            {
                mc.player.addDeltaMovement(newVelo);
            }
        }
    }

    @Inject(
            method = "handleAddEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/PacketUtils;" +
                            "ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;" +
                            "Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
                    shift = At.Shift.AFTER))
    private void handleAddEntityHook(ClientboundAddEntityPacket packet, CallbackInfo info)
    {
        AddEntityEvent event = new AddEntityEvent(
                new Vec3(packet.getX(), packet.getY(), packet.getZ()),
                packet.getId(),
                packet.getType()
        );

        EventBus.getInstance().post(event);
    }
}