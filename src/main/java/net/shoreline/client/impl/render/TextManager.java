package net.shoreline.client.impl.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.state.gui.GlyphRenderState;
import net.shoreline.client.api.interfaces.Globals;

public class TextManager implements Globals
{
    public void drawVanillaString(GuiGraphicsExtractor graphics,
                                  String string,
                                  float x,
                                  float y,
                                  int color)
    {
        graphics.guiRenderState.nextStratum();

        Font.PreparedText text = mc.font.prepareText(string, x, y, color, true, 0);
        text.visit(new Font.GlyphVisitor()
        {
            @Override
            public void acceptGlyph(TextRenderable.Styled glyph)
            {
                graphics.guiRenderState.addGlyphToCurrentLayer(new GlyphRenderState(graphics.pose(), glyph, graphics.scissorStack.peek()));
            }
        });

        graphics.nextStratum();
    }

    public int getHeight()
    {
        return mc.font.lineHeight;
    }

    public int getWidth(String str)
    {
        return mc.font.width(str);
    }
}
