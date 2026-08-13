package net.shoreline.client.asm.mixins.render;

import com.mojang.blaze3d.opengl.GlBuffer;
import net.shoreline.client.asm.ducks.render.IGLBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GlBuffer.class)
public abstract class MixinGLBuffer implements IGLBuffer
{
    @Override
    @Accessor(value = "handle")
    public abstract int shoreline$getHandle();
}
