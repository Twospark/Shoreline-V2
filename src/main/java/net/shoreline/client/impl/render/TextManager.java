package net.shoreline.client.impl.render;

import lombok.Getter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.state.gui.GlyphRenderState;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.client.impl.gui.font.CustomFontRenderer;
import net.shoreline.client.impl.modules.client.FontModule;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;

@Getter
public class TextManager implements Globals
{
    private CustomFontRenderer renderer = new CustomFontRenderer("Verdana", 9);

    public TextManager()
    {
        EventBus.getInstance().subscribe(this);
    }

    public void drawString(GuiGraphicsExtractor graphics,
                           String string,
                           float x,
                           float y,
                           int color)
    {
        if (FontModule.INSTANCE.isEnabled())
        {
            renderer.drawStringWithShadow(graphics, string, x, y, color);
        }
        else
        {
            drawVanillaString(graphics, string, x, y, color);
        }
    }

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
        if (FontModule.INSTANCE.isEnabled())
        {
            return (int) renderer.getStringHeight();
        }

        return mc.font.lineHeight;
    }

    public int getWidth(String str)
    {
        if (FontModule.INSTANCE.isEnabled())
        {
            return renderer.getStringWidth(str);
        }

        return mc.font.width(str);
    }

    public void setRenderer(String str, int size)
    {
        renderer = new CustomFontRenderer(str, size);
    }
}
