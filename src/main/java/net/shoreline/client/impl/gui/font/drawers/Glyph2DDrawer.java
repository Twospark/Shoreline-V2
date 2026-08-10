package net.shoreline.client.impl.gui.font.drawers;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.gui.font.CharLocation;
import net.shoreline.client.impl.gui.font.Glyph;
import net.shoreline.client.impl.gui.font.GlyphCache;
import net.shoreline.client.impl.gui.font.GlyphDrawer;
import net.shoreline.client.impl.render.rect.RectBuilder;
import org.joml.Matrix3x2f;

import java.util.List;

@RequiredArgsConstructor
public class Glyph2DDrawer implements GlyphDrawer, Globals
{
    private final GuiGraphicsExtractor graphics;

    @Override
    public void drawGlyphs(List<CharLocation> locations, Identifier identifier)
    {
        Matrix3x2f matrix = new Matrix3x2f(graphics.pose());
        AbstractTexture texture = mc.getTextureManager().getTexture(identifier);
        TextureSetup setup = TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler());
        for (CharLocation location : locations)
        {
            float xo = location.x();
            float yo = location.y();
            Glyph glyph = location.glyph();
            GlyphCache owner = glyph.owner();
            float w = glyph.width();
            float h = glyph.height();
            float u1 = (float) glyph.textureWidth() / owner.getWidth();
            float v1 = (float) glyph.textureHeight() / owner.getHeight();
            float u2 = (float) (glyph.textureWidth() + glyph.width()) / owner.getWidth();
            float v2 = (float) (glyph.textureHeight() + glyph.height()) / owner.getHeight();

            RectBuilder rect = new RectBuilder()
                    .setTexture(setup)
                    .setMatrix(matrix)
                    .setPosition(xo, yo, xo + w, yo + h)
                    .setTexture(setup, u1, v1, u2, v2)
                    .setPipeline(RenderPipelines.GUI_TEXTURED)
                    .setColor(location.color())
                    .setScissor(graphics.scissorStack.peek());

            graphics.guiRenderState.addGuiElement(rect);
        }
    }
}