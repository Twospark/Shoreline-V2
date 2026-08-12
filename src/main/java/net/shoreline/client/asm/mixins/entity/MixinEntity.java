package net.shoreline.client.asm.mixins.entity;

import net.minecraft.world.entity.Entity;
import net.shoreline.client.impl.event.render.RenderOnFireEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity
{
    @Inject(method = "displayFireAnimation", at = @At(value = "HEAD"), cancellable = true)
    private void displayFireAnimationHook(CallbackInfoReturnable<Boolean> cir)
    {
        RenderOnFireEvent renderFireEntityEvent = new RenderOnFireEvent();
        EventBus.getInstance().post(renderFireEntityEvent);
        if (renderFireEntityEvent.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }
}
