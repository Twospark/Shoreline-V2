package net.shoreline.client.asm.mixins.sodium;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.shoreline.client.impl.event.render.RenderBlockEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public class MixinBlockRenderer
{
    @Inject(
            method = "renderModel",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void onRenderModel(BlockStateModel model,
                               BlockState state,
                               BlockPos pos,
                               BlockPos origin,
                               CallbackInfo info)
    {
        if (Minecraft.getInstance().player != null)
        {
            RenderBlockEvent event = new RenderBlockEvent(state);
            EventBus.getInstance().post(event);
            if (event.isCanceled())
            {
                info.cancel();
            }
        }
    }
}