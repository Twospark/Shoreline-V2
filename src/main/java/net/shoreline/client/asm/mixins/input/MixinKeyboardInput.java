package net.shoreline.client.asm.mixins.input;

import net.minecraft.client.player.KeyboardInput;
import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.input.PlayerInputEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends MixinClientInput
{
}
