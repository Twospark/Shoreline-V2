package net.shoreline.client.asm.mixins.gui;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.shoreline.client.asm.ducks.gui.IChatComponent;
import net.shoreline.client.asm.ducks.gui.IGuiMessage;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class MixinChatComponent implements IChatComponent
{
    @Shadow
    protected abstract void addMessage(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag);

    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;
    @Shadow
    @Final
    private List<GuiMessage> allMessages;
    @Unique
    private int currentId;

    @Override
    public void shoreline$sendMessage(Component component, GuiMessageTag tag, int id)
    {
        currentId = id;
        addMessage(component, null, GuiMessageSource.SYSTEM_CLIENT, tag);
        currentId = 0;
    }

    @Inject(
            method = "addMessageToDisplayQueue",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER))
    private void addMessageToDisplayQueueHook(GuiMessage message, CallbackInfo info)
    {
        ((IGuiMessage) (Object) trimmedMessages.getFirst()).shoreline$setId(currentId);
    }

    @Inject(
            method = "addMessageToQueue",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER))
    private void addMessageToQueueHook(GuiMessage message, CallbackInfo info)
    {
        ((IGuiMessage) (Object) allMessages.getFirst()).shoreline$setId(currentId);
    }

    @Inject(
            method = "addMessage",
            at = @At(
                    value = "HEAD",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;" +
                            "addMessageToQueue(Lnet/minecraft/client/multiplayer/chat/GuiMessage;)V"))
    private void addMessageHook(Component contents,
                                MessageSignature signature,
                                GuiMessageSource source,
                                GuiMessageTag tag,
                                CallbackInfo info)
    {
        if (currentId == 0)
        {
            return;
        }

        trimmedMessages.removeIf(msg ->
                ((IGuiMessage) (Object) msg).shoreline$getId() == currentId);
        allMessages.removeIf(msg ->
                ((IGuiMessage) (Object) msg).shoreline$getId() == currentId);
    }
}
