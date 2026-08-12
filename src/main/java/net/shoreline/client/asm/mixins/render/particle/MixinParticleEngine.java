package net.shoreline.client.asm.mixins.render.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.shoreline.client.impl.event.render.ParticleEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public class MixinParticleEngine
{
    @Inject(method = "createParticle", at = @At(value = "HEAD"), cancellable = true)
    private void createParticleHook(ParticleOptions options,
                                    double x,
                                    double y,
                                    double z,
                                    double xa,
                                    double ya,
                                    double za,
                                    CallbackInfoReturnable<Particle> cir)
    {
        ParticleEvent event = new ParticleEvent(options.getType());
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(null);
        }
    }
}
