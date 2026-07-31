package net.shoreline.client.asm.mixins.connection;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.asm.ducks.connection.IClientPacketListener;
import net.shoreline.client.asm.ducks.connection.IConnection;
import net.shoreline.client.impl.event.network.RotationUpdateEvent;
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
}
