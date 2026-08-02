package net.shoreline.client.asm.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.shoreline.client.impl.event.entity.player.JumpEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity
{
    @Inject(method = "jumpFromGround", at = @At(value = "HEAD"), cancellable = true)
    private void jumpFromGroundHook(CallbackInfo info)
    {
        // noinspection ConstantConditions
        if (LocalPlayer.class.isInstance(this))
        {
            JumpEvent.Pre event = new JumpEvent.Pre();
            EventBus.getInstance().post(event);
            if (event.isCanceled())
            {
                info.cancel();
            }
        }
    }

    @Inject(method = "jumpFromGround", at = @At(value = "TAIL"))
    private void jumpFromGroundHook_Tail(CallbackInfo info)
    {
        // noinspection ConstantConditions
        if (LocalPlayer.class.isInstance(this))
        {
            JumpEvent.Post event = new JumpEvent.Post();
            EventBus.getInstance().post(event);
        }
    }

    @ModifyExpressionValue(
            method = "jumpFromGround",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F"))
    private float jumpFromGroundHook_Yaw(float original)
    {
        // noinspection ConstantConditions
        if (LocalPlayer.class.isInstance(this))
        {
            JumpEvent.Yaw event = new JumpEvent.Yaw(original);
            EventBus.getInstance().post(event);
            if (event.isCanceled())
            {
                return event.getYaw();
            }
        }

        return original;
    }
}
