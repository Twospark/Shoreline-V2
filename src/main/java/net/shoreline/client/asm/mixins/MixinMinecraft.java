package net.shoreline.client.asm.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.thread.ShorelineExecutor;
import net.shoreline.client.asm.ducks.IMinecraft;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.client.impl.event.LevelEvent;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.render.ScreenEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft
{
    @Override
    @Invoker(value = "startUseItem")
    public abstract void shoreline$startUseItem();


    @Override
    @Accessor(value = "rightClickDelay")
    public abstract int shoreline$getRightClickDelay();

    @Override
    @Accessor(value = "rightClickDelay")
    public abstract void shoreline$setRightClickDelay(int delay);

    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;" +
                            "setOverlay(Lnet/minecraft/client/gui/screens/Overlay;)V",
            shift = At.Shift.AFTER))
    private static void ctrHook_Finished(GameConfig gameConfig, CallbackInfo ci)
    {
        Shoreline.postInit();
        ClientEvent.McLoaded event = new ClientEvent.McLoaded();
        EventBus.getInstance().post(event);
    }

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

    @Inject(method = "setLevel", at = @At(value = "TAIL"))
    private void setLevelHook(ClientLevel level, CallbackInfo info)
    {
        LevelEvent.Join event = new LevelEvent.Join();
        EventBus.getInstance().post(event);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At(value = "TAIL"))
    private void disconnectHook(Screen screen, boolean keepResourcePacks, CallbackInfo info)
    {
        LevelEvent.Disconnect event = new LevelEvent.Disconnect();
        EventBus.getInstance().post(event);
    }

    @Inject(method = "setScreen", at = @At(value = "HEAD"))
    private void setScreenHook(Screen screen, CallbackInfo info)
    {
        ScreenEvent event = new ScreenEvent(screen);
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