package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screens.Screen;

@RequiredArgsConstructor
@Getter
public class ScreenEvent
{
    private final Screen screen;
}
