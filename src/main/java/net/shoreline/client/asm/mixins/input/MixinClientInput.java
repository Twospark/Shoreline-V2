package net.shoreline.client.asm.mixins.input;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientInput.class)
public abstract class MixinClientInput
{
    @Shadow
    public Vec2 moveVector;
}
