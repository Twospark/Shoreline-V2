package net.shoreline.client.asm.mixins.gui.screen;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.command.manager.CommandManager;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public abstract class MixinCommandSuggestions
{
    @Shadow
    private @Nullable ParseResults<ClientSuggestionProvider> currentParse;

    @Shadow
    @Final
    private EditBox input;

    @Shadow
    private CommandSuggestions.@Nullable SuggestionsList suggestions;

    @Shadow
    private boolean keepSuggestions;

    @Shadow
    private @Nullable CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    public abstract void showSuggestions(boolean immediateNarration);

    @Inject(
            method = "updateCommandInfo",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/StringReader;canRead()Z",
                    remap = false),
            cancellable = true)
    private void updateCommandInfoHook(CallbackInfo info, @Local(name = "reader") StringReader stringReader)
    {
        CommandManager commandManager = Managers.COMMANDS;
        if (stringReader.getString().startsWith(commandManager.getChatPrefix(), stringReader.getCursor()))
        {
            stringReader.setCursor(stringReader.getCursor() + 1);
            if (currentParse == null)
            {
                currentParse = commandManager.getDispatcher().parse(stringReader, commandManager.getSource());
            }

            int cursor = input.getCursorPosition();
            if (cursor >= 1 && (suggestions == null || !keepSuggestions))
            {
                pendingSuggestions = commandManager.getDispatcher().getCompletionSuggestions(currentParse, cursor);
                pendingSuggestions.thenRun(() ->
                {
                    if (pendingSuggestions.isDone())
                    {
                        showSuggestions(false);
                    }
                });
            }

            info.cancel();
        }
    }
}
