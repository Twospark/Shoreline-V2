package net.shoreline.client.asm.mixins.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.shoreline.client.impl.event.render.HudOverlayEvent;
import net.shoreline.client.impl.modules.render.NoRenderModule;
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
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.getPortalOverlay().getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "extractSpyglassOverlay", at = @At(value = "HEAD"), cancellable = true)
    private void extractSpyglassOverlayHook(GuiGraphicsExtractor graphics, float scale, CallbackInfo info)
    {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.getSpyglassOverlay().getValue()) {
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
            if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.getFrostbiteOverlay().getValue()) {
                info.cancel();
            }
        }
    }

    @Inject(method = "extractEffects", at = @At(value = "HEAD"), cancellable = true)
    private void extractEffectsHook(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo info)
    {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.getPotionsHud().getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "extractSelectedItemName", at = @At(value = "HEAD"), cancellable = true)
    private void extractSelectedItemNameHook(GuiGraphicsExtractor graphics, CallbackInfo info)
    {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.getItemName().getValue()) {
            info.cancel();
        }
    }
}
