package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class HudOverlayEvent
{
    @RequiredArgsConstructor
    @Getter
    public static class Post {
        private final GuiGraphicsExtractor graphics;
        private final float partialTicks;
    }
}