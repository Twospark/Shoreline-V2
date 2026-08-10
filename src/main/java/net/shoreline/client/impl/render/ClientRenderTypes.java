package net.shoreline.client.impl.render;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class ClientRenderTypes
{
    public static final RenderType QUADS = RenderType.create(
            "shoreline_quads", RenderSetup.builder(ClientPipelines.QUADS)
                    .createRenderSetup());

    public static final RenderType DEBUG_LINES = RenderType.create(
            "shoreline_debug_lines", RenderSetup.builder(ClientPipelines.DEBUG_LINES)
                    .createRenderSetup());

    public static final Function<Identifier, RenderType> FONT = Util.memoize((identifier ->
    {
        RenderSetup renderSetup = RenderSetup.builder(RenderPipelines.GUI_TEXTURED)
                .withTexture("Sampler0", identifier)
                .useOverlay().createRenderSetup();

        return RenderType.create("shoreline_font", renderSetup);
    }));
}
