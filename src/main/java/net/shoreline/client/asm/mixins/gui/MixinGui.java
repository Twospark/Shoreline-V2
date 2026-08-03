package net.shoreline.client.asm.mixins.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.shoreline.client.impl.event.render.HudOverlayEvent;
import net.shoreline.client.impl.event.render.OverlayEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui
{
    @Shadow
    @Final
    private static Identifier POWDER_SNOW_OUTLINE_LOCATION;

    @Inject(method = "extractRenderState", at = @At(value = "RETURN"))
    private void extractRenderStateHook(GuiGraphicsExtractor graphics,
                                        DeltaTracker deltaTracker,
                                        CallbackInfo info)
    {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        HudOverlayEvent.Post event = new HudOverlayEvent.Post(graphics, partialTicks);
        EventBus.getInstance().post(event);
    }

    @Inject(method = "extractPortalOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void extractPortalOverlayHook(GuiGraphicsExtractor graphics, float alpha, CallbackInfo info)
    {
        OverlayEvent.Portal event = new OverlayEvent.Portal();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
        }
    }

    @Inject(method = "extractSpyglassOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void extractSpyglassOverlayHook(GuiGraphicsExtractor graphics, float scale, CallbackInfo info)
    {
        OverlayEvent.Spyglass event = new OverlayEvent.Spyglass();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
        }
    }

    @Inject(method = "extractTextureOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void extractTextureOverlayHook_Snow(GuiGraphicsExtractor graphics,
                                                Identifier texture,
                                                float alpha,
                                                CallbackInfo info)
    {
        if (texture.getPath().equals(POWDER_SNOW_OUTLINE_LOCATION.getPath()))
        {
            OverlayEvent.Frostbite event = new OverlayEvent.Frostbite();
            EventBus.getInstance().post(event);
            if (event.isCanceled())
            {
                info.cancel();
            }
        }
    }
}
