package net.shoreline.client.asm.mixins.connection;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.shoreline.client.impl.event.input.PlayerInputEvent;
import net.shoreline.client.impl.event.network.MovementPacketsEvent;
import net.shoreline.client.impl.event.network.PlayerUpdateEvent;
import net.shoreline.client.impl.event.network.SetHandEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer
{
    @Shadow
    private int positionReminder;

    @Shadow
    public ClientInput input;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V",
                    shift = At.Shift.BEFORE))
    private void prePlayerTickHook(CallbackInfo info)
    {
        PlayerUpdateEvent.Pre event = new PlayerUpdateEvent.Pre();
        EventBus.getInstance().post(event);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V",
                    shift = At.Shift.AFTER))
    private void postPlayerTickHook(CallbackInfo info)
    {
        PlayerUpdateEvent.Peri event = new PlayerUpdateEvent.Peri();
        EventBus.getInstance().post(event);
    }

    @Inject(method = "sendPosition", at = @At(value = "HEAD"))
    private void sendPositionHook(CallbackInfo info)
    {
        PlayerUpdateEvent.PrePacket event = new PlayerUpdateEvent.PrePacket();
        EventBus.getInstance().post(event);

        MovementPacketsEvent.Update packetsEvent = new MovementPacketsEvent.Update();
        EventBus.getInstance().post(packetsEvent);
        if (packetsEvent.isCanceled())
        {
            positionReminder = 20;
        }
    }

    @Inject(method = "sendPosition", at = @At(value = "TAIL"))
    private void sendPositionHook_Tail(CallbackInfo info)
    {
        PlayerUpdateEvent.Post event = new PlayerUpdateEvent.Post();
        EventBus.getInstance().post(event);
    }

    @Redirect(
            method = "sendPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;" +
                            "send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void sendPositionPacketsHook(ClientPacketListener instance, Packet<?> packet)
    {
        MovementPacketsEvent.Send event = new MovementPacketsEvent.Send(packet);
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            instance.send(event.getPacket());
            return;
        }

        instance.send(packet);
    }

    @Inject(method = "startUsingItem", at = @At(value = "HEAD"))
    private void startUsingItemHook(InteractionHand hand, CallbackInfo info)
    {
        SetHandEvent event = new SetHandEvent();
        EventBus.getInstance().post(event);
    }

    @Inject(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/ClientInput;tick()V",
                    shift = At.Shift.AFTER))
    private void clientInputTickHook(CallbackInfo info)
    {
        PlayerInputEvent event = new PlayerInputEvent(input);
        EventBus.getInstance().post(event);
    }
}
