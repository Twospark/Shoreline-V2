package net.shoreline.client.api.command.argtype;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.SettingGroup;

import java.util.concurrent.CompletableFuture;

public class SettingArgumentType implements ArgumentType<Setting<?>>
{
    private final Module module;

    /** Use factory method. */
    private SettingArgumentType(Module module)
    {
        this.module = module;
    }

    public static SettingArgumentType config(Module module)
    {
        return new SettingArgumentType(module);
    }

    public static Setting<?> getConfig(CommandContext<?> context, String name)
    {
        return context.getArgument(name, Setting.class);
    }

    @Override
    public Setting<?> parse(StringReader reader) throws CommandSyntaxException
    {
        String name = reader.readString();
        Setting<?> result = null;
        for (Setting<?> config : module.getSettings())
        {
            if (config instanceof SettingGroup)
            {
                continue;
            }

            if (config.getName().equalsIgnoreCase(name))
            {
                result = config;
                break;
            }
        }

        if (result == null)
        {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader, null);
        }

        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
                                                              SuggestionsBuilder builder)
    {
        for (Setting<?> config : module.getSettings())
        {
            if (config instanceof SettingGroup)
            {
                continue;
            }

            builder.suggest(config.getName().toLowerCase());
        }

        return builder.buildFuture();
    }
}