package net.shoreline.client.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;
import net.shoreline.client.api.command.Command;
import net.shoreline.client.api.command.argtype.ModuleArgumentType;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.util.Bind;
import net.shoreline.client.util.input.Keyboard;
import org.lwjgl.glfw.GLFW;

public class BindCommand extends Command
{
    public BindCommand()
    {
        super("bind", new String[] {"keybind"}, "Binds a module to a key");
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<ClientSuggestionProvider> argumentBuilder)
    {
        argumentBuilder.then(buildArgument("module", ModuleArgumentType.module())
            .then(buildArgument("bind", StringArgumentType.string())
                .executes(c ->
                {
                    Module module = ModuleArgumentType.getModule(c, "module");
                    if (module instanceof Toggleable toggleable)
                    {
                        String bind = StringArgumentType.getString(c, "bind");
                        if (bind == null)
                        {
                            sendErrorChatMessage("Invalid key!");
                            return 0;
                        }

                        int keycode = Keyboard.fromString(bind);
                        if (keycode == GLFW.GLFW_KEY_UNKNOWN)
                        {
                            sendErrorChatMessage("Failed to parse key!");
                            return 0;
                        }

                        toggleable.setBind(Bind.fromKey(keycode));
                        sendClientChatMessage(ChatFormatting.GRAY + module.getName() + ChatFormatting.RESET + " is now bound to " + ChatFormatting.GOLD + bind.toUpperCase());
                    }

                    return 1;
                }))
                .executes(c ->
                {
                    sendErrorChatMessage("Must provide a module to keybind!");
                    return 1;
                }))
                .executes(c ->
                {
                    sendErrorChatMessage("Invalid usage! Usage: bind <module> <key_name>");
                    return 1;
                });
    }
}
