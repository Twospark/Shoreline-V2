package net.shoreline.client.asm.mixins.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.shoreline.client.impl.modules.render.NoRenderModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrackingEmitter.class)
public class MixinTrackingEmitter
{
    @Mutable
    @Shadow
    @Final
    private int lifeTime;

    @Shadow
    @Final
    private ParticleOptions particleType;

    @Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;)V", at = @At(value = "RETURN"))
    private void ctrHook(ClientLevel level, Entity entity, ParticleOptions particleType, CallbackInfo info)
    {
        if (particleType != ParticleTypes.TOTEM_OF_UNDYING) return;

        if (NoRenderModule.INSTANCE.isEnabled()) {
            if (NoRenderModule.INSTANCE.getTotemEffects().getValue()) {
                this.lifeTime = 0;
            } else {
                this.lifeTime = NoRenderModule.INSTANCE.getTotemTicks().getValue();
            }
        }
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 16))
    private int hookTickCount(int constant)
    {
        if (this.particleType != ParticleTypes.TOTEM_OF_UNDYING) return constant;

        if (NoRenderModule.INSTANCE.isEnabled()) {
            if (NoRenderModule.INSTANCE.getTotemEffects().getValue()) {
                return 0;
            }
            return NoRenderModule.INSTANCE.getTotemParticles().getValue();
        }
        return constant;
    }
}
