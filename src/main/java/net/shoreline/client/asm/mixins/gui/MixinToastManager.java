package net.shoreline.client.asm.mixins.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.shoreline.client.impl.event.render.RenderGuiToastEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public class MixinToastManager
{
    @Inject(method = "extractRenderState", at = @At(value = "HEAD"), cancellable = true)
    private void extractRenderStateHook(GuiGraphicsExtractor graphics, CallbackInfo info)
    {
        RenderGuiToastEvent renderToastEvent = new RenderGuiToastEvent();
        EventBus.getInstance().post(renderToastEvent);
        if (renderToastEvent.isCanceled())
        {
            info.cancel();
        }
    }
}
