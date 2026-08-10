package net.shoreline.client.impl.gui.font;

public record Glyph(int textureWidth, int textureHeight, int width, int height, char value, GlyphCache owner) {}