package net.shoreline.client.asm.mixins.render;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.shoreline.client.asm.ducks.render.IRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.class)
public abstract class MixinRenderType implements IRenderType
{
    @Override
    @Accessor(value = "state")
    public abstract RenderSetup shoreline$getState();
}
