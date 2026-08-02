package net.shoreline.client.impl.event.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.shoreline.client.impl.render.ClientRenderer;

@RequiredArgsConstructor
@Getter
public class RenderWorldEvent
{
    private final ClientRenderer renderer;
}
