package net.shoreline.client.asm.mixins.render;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;
import net.shoreline.client.impl.event.render.RenderFloatingItemEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
    @Inject(method = "displayItemActivation", at = @At(value = "HEAD"), cancellable = true)
    private void displayItemActivationHook(ItemStack itemStack, CallbackInfo info)
    {
        RenderFloatingItemEvent event = new RenderFloatingItemEvent(itemStack);
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            info.cancel();
        }
    }
}
