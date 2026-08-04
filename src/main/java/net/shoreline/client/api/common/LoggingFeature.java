package net.shoreline.client.api.common;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.shoreline.client.api.gui.Theme;
import net.shoreline.client.asm.ducks.gui.IChatComponent;
import net.shoreline.client.impl.modules.client.ThemeModule;

public class LoggingFeature extends Feature
{
    protected static final String RAW_PREFIX = "[Shoreline]";
    protected static final String ERROR_PREFIX = "[\u274C] ";
    protected static final String SUCCESS_PREFIX = "[\u2713] ";

    public LoggingFeature(String name)
    {
        super(name);
    }

    public LoggingFeature(String name, String[] nameAliases)
    {
        super(name, nameAliases);
    }

    protected void sendClientMessageWithOptionalDeletion(String message, int id)
    {
        sendChatMessageWithOptionalDeletion(wrap(Component.literal(message)), ThemeModule.INSTANCE.getPrimary(), id);
    }

    protected void sendClientChatMessage(String message)
    {
        sendChatText(wrap(Component.literal(message)), ThemeModule.INSTANCE.getPrimary());
    }

    protected void sendClientTextMessage(Component text)
    {
        sendChatText(wrap(text), ThemeModule.INSTANCE.getPrimary());
    }

    protected void sendChatMessage(String message)
    {
        sendChatMessage(message, ThemeModule.INSTANCE.getPrimary());
    }

    protected void sendChatText(Component text)
    {
        sendChatText(text, ThemeModule.INSTANCE.getPrimary());
    }

    protected void sendSuccessChatMessage(String message)
    {
        sendChatMessage(ChatFormatting.GREEN + SUCCESS_PREFIX + message, CommonColors.GREEN);
    }

    protected void sendErrorChatMessage(String message)
    {
        sendChatMessage(ChatFormatting.RED + ERROR_PREFIX + message, CommonColors.RED);
    }

    protected void sendChatMessage(String message, int color)
    {
        sendChatText(Component.literal(message), color);
    }

    protected void sendChatText(Component component, int color)
    {
        runOnThread(() -> mc.gui.getChat().addMessage(component, null, GuiMessageSource.SYSTEM_CLIENT,
                new GuiMessageTag(color, null, Component.empty(), "CLIENT")));
    }

    protected void sendChatMessageWithOptionalDeletion(Component component, int color, int id)
    {
        runOnThread(() -> ((IChatComponent) mc.gui.getChat()).shoreline$sendMessage(component,
                new GuiMessageTag(color, null, Component.empty(), "CLIENT"), id));
    }

    private Component wrap(Component component)
    {
        return Component.literal(RAW_PREFIX)
                .withStyle(style -> style.withColor(Theme.getInstance().getPrimary()))
                .append(" ")
                .append(component);
    }
}
