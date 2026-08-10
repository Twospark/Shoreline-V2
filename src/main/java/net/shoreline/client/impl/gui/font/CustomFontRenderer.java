package net.shoreline.client.impl.gui.font;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.Identifier;
import net.shoreline.client.ShorelineMod;
import net.shoreline.client.impl.gui.font.drawers.Glyph2DDrawer;
import net.shoreline.client.impl.gui.font.drawers.Glyph3DDrawer;
import net.shoreline.client.impl.modules.client.FontModule;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CustomFontRenderer implements Closeable
{
    private final ObjectList<GlyphCache> caches = new ObjectArrayList<>();
    private final Char2ObjectArrayMap<Glyph> glyphs = new Char2ObjectArrayMap<>();
    private final Map<Identifier, ObjectList<CharLocation>> cache = new Object2ObjectOpenHashMap<>();

    private Font font;
    private final float size;

    private int scale;

    public CustomFontRenderer(String name, float size)
    {
        this.font = new Font(name, Font.PLAIN, Math.round(size));
        this.size = size;
        createFont(font, size);
    }

    private void createFont(Font font, float size)
    {
        this.scale = Minecraft.getInstance().getWindow().getGuiScale();
        this.font = font.deriveFont(size * scale);
    }

    /* -------------------- 2D Rendering -------------------- */

    public void drawStringWithShadow(GuiGraphicsExtractor graphics, String str, float x, float y, int color)
    {
        drawString(graphics, str, x + 0.5f, y + 0.5f, color, true);
        drawString(graphics, str, x, y, color, false);
    }

    public void drawString(GuiGraphicsExtractor graphics, String str, float x, float y, int color, boolean shadow)
    {
        float scaled = 1.0f / scale;
        Matrix3x2fStack matrix = graphics.pose();
        matrix.pushMatrix();
        matrix.translate(x, y);
        matrix.scale(scaled);

        prepare(str, color, shadow, new Glyph2DDrawer(graphics));

        matrix.popMatrix();
    }

    /* -------------------- 3D Rendering -------------------- */

    public void drawStringWithShadow(PoseStack pose, MultiBufferSource.BufferSource bufferSource, String str, float x, float y, int color)
    {
        drawString(pose, bufferSource, str, x + 0.5f, y + 0.5f, color, true);
        drawString(pose, bufferSource, str, x, y, color, false);
    }

    public void drawString(PoseStack pose, MultiBufferSource.BufferSource bufferSource, String str, float x, float y, int color, boolean shadow)
    {
        float scaled = 1.0f / scale;
        pose.pushPose();
        pose.translate(x, y, 0.0f);
        pose.scale(scaled, scaled, 0.0f);

        prepare(str, color, shadow, new Glyph3DDrawer(bufferSource, pose));

        pose.popPose();
    }

    /* -------------------- Utility -------------------- */

    public void prepare(String str, int clr, boolean shadow, GlyphDrawer drawer)
    {
        ensureScale();

        boolean formatting = false;

        int baseColor    = shadow ? getShadow(clr) : clr;
        int currentColor = baseColor;

        char[] chars = str.toCharArray();
        float xOffset = 0.0f;
        float yOffset = -1f + FontModule.INSTANCE.getFontOffset().getValue();

        for (char chr : chars)
        {
            if (formatting)
            {
                if (chr == 'r')
                {
                    currentColor = baseColor;
                }
                else
                {
                    int colorCode = getColorFromCode(chr);
                    currentColor = shadow ? getShadow(colorCode) : colorCode;
                }

                formatting = false;
                continue;
            }

            if (chr == '§')
            {
                formatting = true;
                continue;
            }

            Glyph glyph = glyphs.computeIfAbsent(chr, this::getGlyphFromChar);
            if (glyph != null)
            {
                if (glyph.value() != ' ')
                {
                    Identifier identifier = glyph.owner().getId();
                    CharLocation entry = new CharLocation(xOffset, yOffset, currentColor, glyph);
                    cache.computeIfAbsent(identifier, _ -> new ObjectArrayList<>()).add(entry);
                }

                xOffset += glyph.width();
            }
        }

        for (Map.Entry<Identifier, ObjectList<CharLocation>> entry : cache.entrySet())
        {
            ObjectList<CharLocation> locations = entry.getValue();
            if (!locations.isEmpty())
            {
                drawer.drawGlyphs(locations, entry.getKey());
                locations.clear();
            }
        }
    }

    public int getStringWidth(String text)
    {
        if (text == null)
        {
            return 0;
        }

        float currentLine = 0;
        float maxPreviousLines = 0;
        boolean formatting = false;
        int length = text.length();

        for (int i = 0; i < length; i++)
        {
            char c = text.charAt(i);
            if (c == '\n')
            {
                maxPreviousLines = Math.max(currentLine, maxPreviousLines);
                currentLine = 0;
                continue;
            }

            if (formatting)
            {
                formatting = false;
                if (c == 'y')
                {
                    i = Math.min(i + 8, length - 1);
                }

                continue;
            }

            if (c == '§')
            {
                formatting = true;
                continue;
            }

            Glyph glyph = glyphs.computeIfAbsent(c, this::getGlyphFromChar);
            float w = glyph == null ? 0 : glyph.width();
            currentLine += w / (float) this.scale;
        }

        return Math.round(Math.max(currentLine, maxPreviousLines));
    }

    public float getStringHeight()
    {
        return size;
    }

    public int getShadow(int color)
    {
        return (color & 0xFF000000) | ((color & 0x00FCFCFC) >> 2);
    }

    private int getColorFromCode(char code)
    {
        return switch (Character.toLowerCase(code))
        {
            case '0' -> 0xFF000000;
            case '1' -> 0xFF0000AA;
            case '2' -> 0xFF00AA00;
            case '3' -> 0xFF00AAAA;
            case '4' -> 0xFFAA0000;
            case '5' -> 0xFFAA00AA;
            case '6' -> 0xFFFFAA00;
            case '7' -> 0xFFAAAAAA;
            case '8' -> 0xFF555555;
            case '9' -> 0xFF5555FF;
            case 'a' -> 0xFF55FF55;
            case 'b' -> 0xFF55FFFF;
            case 'c' -> 0xFFFF5555;
            case 'd' -> 0xFFFF55FF;
            case 'e' -> 0xFFFFFF55;
            case 'f' -> 0xFFFFFFFF;
            default -> 0xFFFFFFFF;
        };
    }

    public void ensureScale()
    {
        int currentScale = Minecraft.getInstance().getWindow().getGuiScale();
        if (currentScale != scale)
        {
            close();
            createFont(font, size);
        }
    }

    public Glyph getGlyphFromChar(char c)
    {
        // Return cached glyph
        for (GlyphCache map : caches)
        {
            if (map.contains(c))
            {
                return map.getGlyph(c);
            }
        }

        int base = 256 * (int) Math.floor((double) c / (double) 256);
        GlyphCache glyphCache = new GlyphCache((char) base, (char) (base + 256), font, getGlyphIdentifier(), 5,
                FontModule.INSTANCE.getAntiAlias().getValue(), FontModule.INSTANCE.getFractionalMetrics().getValue());

        caches.add(glyphCache);
        return glyphCache.getGlyph(c);
    }

    public Identifier getGlyphIdentifier()
    {
        return Identifier.fromNamespaceAndPath(ShorelineMod.MOD_ID, "font/storage/" + generateRandomHex(32));
    }

    private static final String HEX_CHARS = "0123456789abcdef";

    public String generateRandomHex(int length)
    {
        StringBuilder hexString = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            int index = ThreadLocalRandom.current().nextInt(HEX_CHARS.length());
            hexString.append(HEX_CHARS.charAt(index));
        }

        return hexString.toString();
    }

    @Override
    public void close()
    {
        try
        {
            for (GlyphCache cache1 : caches)
            {
                cache1.clear();
            }

            caches.clear();
            glyphs.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
