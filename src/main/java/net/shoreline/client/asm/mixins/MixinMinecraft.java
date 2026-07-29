package net.shoreline.client.asm.mixins;

import net.minecraft.client.Minecraft;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.thread.ShorelineExecutor;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tickHook(CallbackInfo info)
    {
        TickEvent event = new TickEvent();
        EventBus.getInstance().post(event);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickHook_Tail(CallbackInfo info)
    {
        TickEvent.Post event = new TickEvent.Post();
        EventBus.getInstance().post(event);
    }

    @Inject(method = "run", at = @At(value = "RETURN"))
    private void shutDownHook(CallbackInfo info)
    {
        Shoreline.info("Shutting down");

        ClientEvent.ShutDown event = new ClientEvent.ShutDown();
        EventBus.getInstance().post(event);

        ShorelineExecutor.SERVICE.shutdown();
    }
}