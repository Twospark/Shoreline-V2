package net.shoreline.client.asm.mixins.render;

import com.mojang.blaze3d.font.GlyphInfo;
import net.shoreline.client.impl.modules.render.NoRenderModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlyphInfo.class)
public interface MixinGlyphInfo
{
    @Inject(method = "getShadowOffset", at = @At(value = "HEAD"), cancellable = true)
    private void hookGetShadowOffset(CallbackInfoReturnable<Float> cir)
    {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.getTextShadow().getValue()) {
            cir.cancel();
            cir.setReturnValue(0.5F);
        }
    }
}
