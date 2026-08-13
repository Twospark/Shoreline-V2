package net.shoreline.client.asm.mixins.render.post;

import com.mojang.blaze3d.buffers.GpuBuffer;
import net.minecraft.client.renderer.PostPass;
import net.shoreline.client.asm.ducks.render.IPostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PostPass.class)
public abstract class MixinPostPass implements IPostPass
{
    @Override
    @Accessor(value = "customUniforms")
    public abstract Map<String, GpuBuffer> shoreline$getUniformBuffers();
}
