package net.shoreline.client.asm.mixins.render.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.render.EntityHurtEvent;
import net.shoreline.client.impl.modules.client.RotationsModule;
import net.shoreline.client.impl.rotation.handler.RotationRenderer;
import net.shoreline.eventbus.EventBus;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity,
        S extends LivingEntityRenderState,
        M extends EntityModel<? super S>>
        implements Globals
{
    @Unique
    float yaw;
    @Unique
    float rotationYaw;
    @Unique
    float rotationPitch;
    @Unique
    float bodyYaw;
    @Unique
    float prevBodyYaw;
    @Unique
    float prevRotationYaw;
    @Unique
    float prevRotationPitch;

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;" +
                    "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At(value = "HEAD"))
    private void extractRenderStateHook_Head(T entity, S state, float partialTicks, CallbackInfo info)
    {
        if (entity == mc.player && RotationsModule.INSTANCE.getShowRotations().getValue())
        {
            yaw               = entity.getYRot();
            rotationYaw       = entity.yHeadRot;
            prevRotationYaw   = entity.yHeadRotO;
            rotationPitch     = entity.getXRot();
            prevRotationPitch = entity.xRotO;
            bodyYaw           = entity.yBodyRot;
            prevBodyYaw       = entity.yBodyRotO;

            RotationRenderer renderer = Managers.ROTATION.getRenderer();
            entity.setYRot(renderer.getYaw());
            entity.setXRot(renderer.getPitch());
            entity.xRotO     = renderer.getPrevPitch();
            entity.yHeadRot  = renderer.getYaw();
            entity.yHeadRotO = renderer.getPrevYaw();
            entity.yRotO     = renderer.getPrevYaw();
            entity.yBodyRot  = renderer.getYawOffset();
            entity.yBodyRotO = renderer.getPrevYawOffset();
        }
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;" +
                    "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At(value = "TAIL"))
    private void extractRenderStateHook_Tail(T entity, S state, float partialTicks, CallbackInfo info)
    {
        if (entity == mc.player && RotationsModule.INSTANCE.getShowRotations().getValue())
        {
            entity.setYRot(yaw);
            entity.setXRot(rotationPitch);
            entity.xRotO     = prevRotationPitch;
            entity.yHeadRot  = rotationYaw;
            entity.yHeadRotO = prevRotationYaw;
            entity.yRotO     = prevRotationYaw;
            entity.yBodyRot  = bodyYaw;
            entity.yBodyRotO = prevBodyYaw;
        }
    }

    @Redirect(
            method = "getOverlayCoords",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;hasRedOverlay:Z",
                    opcode = Opcodes.GETFIELD))
    private static boolean hurtHook(LivingEntityRenderState instance)
    {
        EntityHurtEvent event = new EntityHurtEvent();
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            return false;
        }

        return instance.hasRedOverlay;
    }
}