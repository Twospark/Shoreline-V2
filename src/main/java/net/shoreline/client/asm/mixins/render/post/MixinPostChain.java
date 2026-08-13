package net.shoreline.client.asm.mixins.render.post;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.shoreline.client.asm.ducks.render.IPostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PostChain.class)
public abstract class MixinPostChain implements IPostChain
{
    @Override
    @Accessor(value = "passes")
    public abstract List<PostPass> shoreline$getPasses();
}
