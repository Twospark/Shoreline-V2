package net.shoreline.client.impl.command.manager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.permissions.PermissionSet;
import net.shoreline.client.api.command.Command;
import net.shoreline.client.api.registry.OrderedRegistry;
import net.shoreline.client.api.registry.RegistryFeature;
import net.shoreline.client.impl.command.BindCommand;
import net.shoreline.client.impl.command.EnemyCommand;
import net.shoreline.client.impl.command.FriendCommand;
import net.shoreline.client.impl.command.PresetCommand;
import net.shoreline.client.impl.event.network.ChatScreenEvent;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;

@Getter
public class CommandManager extends RegistryFeature<Command>
{
    private String chatPrefix = ".";

    private final CommandDispatcher<ClientSuggestionProvider> dispatcher;
    private final ClientSuggestionProvider source;

    public CommandManager()
    {
        super("Commands", new OrderedRegistry<>());
        this.dispatcher = new CommandDispatcher<>();
        this.source = new ClientSuggestionProvider(mc.getConnection(), mc, PermissionSet.ALL_PERMISSIONS);

        EventBus.getInstance().subscribe(this);

        getRegistry().register(
                new BindCommand(),
                new EnemyCommand(),
                new FriendCommand(),
                new PresetCommand()
        );

        for (Command command : getRegistry().getCollection())
        {
            for (LiteralArgumentBuilder<ClientSuggestionProvider> argumentBuilder : command.getArgumentBuilders())
            {
                command.buildCommand(argumentBuilder);
                dispatcher.register(argumentBuilder);
            }
        }
    }

    @Subscribe(priority = 999)
    public void onChatSendMessage(ChatScreenEvent.SendMessage event)
    {
        String text = event.getMessage().trim();
        if (text.startsWith(chatPrefix))
        {
            event.setCanceled(true);
            mc.gui.getChat().addRecentChat(text);
            try
            {
                String literal = text.substring(1);
                execute(literal);
            }
            catch (Exception exception)
            {
                // exception.printStackTrace();
            }
        }
    }

    public void execute(String string) throws Exception
    {
        dispatcher.execute(string, source);
    }
}
