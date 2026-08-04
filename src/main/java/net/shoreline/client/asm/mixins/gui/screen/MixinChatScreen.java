package net.shoreline.client.asm.mixins.gui.screen;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.shoreline.client.impl.event.network.ChatScreenEvent;
import net.shoreline.eventbus.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinChatScreen
{
    @Inject(method = "handleChatInput", at = @At(value = "HEAD"), cancellable = true)
    private void handleChatInputHook(String msg, boolean addToRecent, CallbackInfo info)
    {
        ChatScreenEvent.SendMessage chatScreenEvent = new ChatScreenEvent.SendMessage(msg);
        EventBus.getInstance().post(chatScreenEvent);
        if (chatScreenEvent.isCanceled())
        {
            info.cancel();
        }
    }
}
