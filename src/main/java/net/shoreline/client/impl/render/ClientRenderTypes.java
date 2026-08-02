package net.shoreline.client.impl.render;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class ClientRenderTypes
{
    public static final RenderType QUADS = RenderType.create(
            "shoreline_quads", RenderSetup.builder(ClientPipelines.QUADS)
                    .createRenderSetup());

    public static final RenderType DEBUG_LINES = RenderType.create(
            "shoreline_debug_lines", RenderSetup.builder(ClientPipelines.DEBUG_LINES)
                    .createRenderSetup());
}
