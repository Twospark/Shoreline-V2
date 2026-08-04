package net.shoreline.client.asm.ducks.gui;

import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;

public interface IChatComponent
{
    void shoreline$sendMessage(Component component, GuiMessageTag tag, int id);
}
