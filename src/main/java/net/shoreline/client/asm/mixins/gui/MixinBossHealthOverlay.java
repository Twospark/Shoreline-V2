package net.shoreline.client.asm.mixins.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.shoreline.client.impl.event.render.OverlayEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class MixinBossHealthOverlay
{
    @Inject(method = "extractRenderState", at = @At(value = "HEAD"), cancellable = true)
    private void extractRenderStateHook(GuiGraphicsExtractor graphics, CallbackInfo info)
    {
        OverlayEvent.BossBar event = new OverlayEvent.BossBar();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
        }
    }
}
