package net.shoreline.client.api.command.argtype;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PlayerArgumentType implements ArgumentType<String>
{
    public static PlayerArgumentType player()
    {
        return new PlayerArgumentType();
    }

    public static String getPlayer(final CommandContext<?> context, final String name)
    {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        String[] literal = context.getInput().split(" ");
        Collection<PlayerInfo> playerListEntries = Minecraft.getInstance().getConnection().getOnlinePlayers();
        for (PlayerInfo playerListEntry : playerListEntries)
        {
            String playerName = playerListEntry.getProfile().name();
            for (String string : literal)
            {
                if (string.isBlank() || playerName.toLowerCase().startsWith(string.toLowerCase()))
                {
                    builder.suggest(playerName);
                    break;
                }
            }
        }

        return builder.buildFuture();
    }
}