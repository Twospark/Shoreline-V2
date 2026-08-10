package net.shoreline.client.asm.mixins.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.shoreline.client.asm.ducks.render.INativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NativeImage.class)
public abstract class MixinNativeImage implements INativeImage
{
    @Override
    @Accessor(value = "pixels")
    public abstract long shoreline$getPixels();
}
