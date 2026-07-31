package net.shoreline.client.asm.mixins.level;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.shoreline.client.asm.ducks.level.IClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel implements IClientLevel
{
    @Override
    @Accessor(value = "blockStatePredictionHandler")
    public abstract BlockStatePredictionHandler shoreline$getBlockStatePredictionHandler();
}
