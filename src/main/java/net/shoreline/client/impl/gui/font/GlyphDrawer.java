package net.shoreline.client.impl.gui.font;

import net.minecraft.resources.Identifier;

import java.util.List;

public interface GlyphDrawer
{
    void drawGlyphs(List<CharLocation> locations, Identifier identifier);
}