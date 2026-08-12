package net.shoreline.client.asm.mixins.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.shoreline.client.impl.event.render.EmitParticleEvent;
import net.shoreline.eventbus.EventBus;
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
        EmitParticleEvent event = new EmitParticleEvent(particleType);
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            lifeTime = event.getMaxTicks();
        }
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 16))
    private int hookTickCount(int constant)
    {
        EmitParticleEvent event = new EmitParticleEvent(particleType);
        EventBus.getInstance().post(event);
        return event.isCanceled() ? event.getMaxCount() : constant;
    }
}
