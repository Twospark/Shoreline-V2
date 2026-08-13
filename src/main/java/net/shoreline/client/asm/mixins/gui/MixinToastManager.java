package net.shoreline.client.asm.mixins.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.shoreline.client.impl.modules.render.NoRenderModule;
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
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.getToastConfig().getValue()) {
            info.cancel();
        }
    }
}
