package net.shoreline.client.asm.mixins.sodium;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.shoreline.client.impl.event.render.RenderBlockEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext", remap = false)
public class MixinAbstractBlockRenderContext
{
    @Shadow
    protected BlockState state;

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true, remap = false)
    private void shouldDrawSideHook(Direction facing, CallbackInfoReturnable<Boolean> cir)
    {
        RenderBlockEvent event = new RenderBlockEvent(state);
        EventBus.getInstance().post(event);
        if (event.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }
}
