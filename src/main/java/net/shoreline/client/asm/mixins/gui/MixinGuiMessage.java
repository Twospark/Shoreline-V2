package net.shoreline.client.asm.mixins.gui;

import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.shoreline.client.asm.ducks.gui.IGuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiMessage.class)
public class MixinGuiMessage implements IGuiMessage
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
