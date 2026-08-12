package net.shoreline.client.asm.mixins.render;

import com.mojang.blaze3d.font.GlyphInfo;
import net.shoreline.client.impl.event.render.GlyphShadowEvent;
import net.shoreline.eventbus.EventBus;
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
        GlyphShadowEvent event = new GlyphShadowEvent();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(event.getShadowOffset());
        }
    }
}
