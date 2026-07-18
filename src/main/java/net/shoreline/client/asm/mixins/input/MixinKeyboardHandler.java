package net.shoreline.client.asm.mixins.input;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import net.shoreline.client.impl.event.input.KeyboardEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler
{
    @Inject(method = "keyPress", at = @At(value = "HEAD"))
    private void onKeyPress(long handle, int action, KeyEvent event, CallbackInfo info)
    {
        int key = event.key();
        if (key == -1)
        {
            return;
        }

        KeyboardEvent keyboardEvent = new KeyboardEvent(key, action, event.modifiers());
        EventBus.getInstance().post(keyboardEvent);
    }
}
