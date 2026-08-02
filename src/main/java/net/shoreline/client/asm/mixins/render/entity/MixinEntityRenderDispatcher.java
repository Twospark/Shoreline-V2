package net.shoreline.client.asm.mixins.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.shoreline.client.impl.event.render.RenderEntityEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher
{
    @Inject(method = "shouldRender", at = @At(value = "HEAD"), cancellable = true)
    private <E extends Entity> void shouldRenderHook(E entity,
                                                     Frustum culler,
                                                     double camX,
                                                     double camY,
                                                     double camZ,
                                                     CallbackInfoReturnable<Boolean> cir)
    {
        RenderEntityEvent event = new RenderEntityEvent(entity);
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }
}
