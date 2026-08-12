package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.eventbus.Event;

public class HudOverlayEvent
{
    @RequiredArgsConstructor
    @Getter
    public static class Post
    {
        private final GuiGraphicsExtractor graphics;
        private final float partialTicks;
    }

    public static class Potions extends Event {}

    public static class ItemName extends Event {}
}
