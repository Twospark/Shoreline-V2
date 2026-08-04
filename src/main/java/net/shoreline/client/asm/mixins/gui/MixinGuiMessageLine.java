package net.shoreline.client.asm.mixins.gui;

import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.shoreline.client.asm.ducks.gui.IGuiMessageLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiMessage.Line.class)
public class MixinGuiMessageLine implements IGuiMessageLine
{
    @Unique
    private int id;

    @Override
    public int shoreline$getId()
    {
        return id;
    }

    @Override
    public void shoreline$setId(int id)
    {
        this.id = id;
    }
}
