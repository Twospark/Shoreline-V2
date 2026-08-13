package net.shoreline.client.impl.gui.font;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import lombok.Getter;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.asm.ducks.render.INativeImage;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GlyphCache implements Globals
{
    private final char start, end;
    private final Font font;
    @Getter
    private final Identifier id;
    private final int padding;
    @Getter
    private int width;
    @Getter
    private int height;
    private final Char2ObjectArrayMap<Glyph> glyphs = new Char2ObjectArrayMap<>();
    private boolean generated;

    private final boolean antiAlias, fractionalMetrics;

    public GlyphCache(char from, char to, Font font, Identifier id, int padding, boolean antiAlias, boolean fractionalMetrics)
    {
        this.start = from;
        this.end = to;
        this.font = font;
        this.id = id;
        this.padding = padding;
        this.antiAlias = antiAlias;
        this.fractionalMetrics = fractionalMetrics;
    }

    public Glyph getGlyph(char c)
    {
        if (!generated)
        {
            createBitmap();
        }
        return glyphs.get(c);
    }

    public void clear()
    {
        mc.getTextureManager().release(id);
        glyphs.clear();
        generated = false;
    }

    public boolean contains(char c)
    {
        return c >= start && c < end;
    }

    public void createBitmap()
    {
        if (generated)
        {
            return;
        }

        List<Glyph> glyphs1 = new ArrayList<>();
        int range = end - start - 1;
        int ceiling = (int) (Math.ceil(Math.sqrt(range)) * 1.5);
        int cached = 0;
        int charX = 0;
        int maxX = 0, maxY = 0;
        int currX = 0, currY = 0;
        int currentRowMaxY = 0;
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), antiAlias, fractionalMetrics);
        while (cached <= range)
        {
            char currentChar = (char) (start + cached);
            Rectangle2D stringBounds = font.getStringBounds(String.valueOf(currentChar), frc);
            int width = (int) Math.ceil(stringBounds.getWidth());
            int height = (int) Math.ceil(stringBounds.getHeight());
            cached++;
            maxX = Math.max(maxX, currX + width);
            maxY = Math.max(maxY, currY + height);
            if (charX >= ceiling)
            {
                currX = 0;
                currY += currentRowMaxY + padding; // add height of highest glyph, and reset
                charX = 0;
                currentRowMaxY = 0;
            }
            currentRowMaxY = Math.max(currentRowMaxY, height); // calculate the highest glyph in this row
            glyphs1.add(new Glyph(currX, currY, width, height, currentChar, this));
            currX += width + padding;
            charX++;
        }

        BufferedImage bufferedImage = new BufferedImage(Math.max(maxX + padding, 1), Math.max(maxY + padding, 1), BufferedImage.TYPE_INT_ARGB);
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setComposite(AlphaComposite.Src);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(Color.WHITE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        for (Glyph glyph : glyphs1)
        {
            g2d.setFont(font);
            FontMetrics fontMetrics = g2d.getFontMetrics();
            g2d.drawString(String.valueOf(glyph.value()), glyph.textureWidth(), glyph.textureHeight() + fontMetrics.getAscent());
            glyphs.put(glyph.value(), glyph);
        }

        g2d.dispose();
        registerTexture(id, bufferedImage);
        generated = true;
    }

    // https://github.com/0x3C50/Renderer
    public void registerTexture(Identifier identifier, BufferedImage bufferedImage)
    {
        try
        {
            int imageWidth = bufferedImage.getWidth();
            int imageHeight = bufferedImage.getHeight();
            NativeImage image = new NativeImage(NativeImage.Format.RGBA, imageWidth, imageHeight, false);
            long pixels = ((INativeImage) (Object) image).shoreline$getPixels();
            IntBuffer backingBuffer = MemoryUtil.memIntBuffer(pixels, image.getWidth() * image.getHeight());
            WritableRaster raster = bufferedImage.getRaster();
            ColorModel colorModel = bufferedImage.getColorModel();
            int bands = raster.getNumBands();
            int dataType = raster.getDataBuffer().getDataType();
            Object dataBuffer = switch (dataType)
            {
                case DataBuffer.TYPE_BYTE -> new byte[bands];
                case DataBuffer.TYPE_USHORT -> new short[bands];
                case DataBuffer.TYPE_INT -> new int[bands];
                case DataBuffer.TYPE_FLOAT -> new float[bands];
                case DataBuffer.TYPE_DOUBLE -> new double[bands];
                default -> throw new IllegalArgumentException("Unknown data buffer type: " + dataType);
            };

            for (int y = 0; y < imageHeight; y++)
            {
                for (int x = 0; x < imageWidth; x++)
                {
                    raster.getDataElements(x, y, dataBuffer);
                    int a = colorModel.getAlpha(dataBuffer);
                    int r = colorModel.getRed(dataBuffer);
                    int g = colorModel.getGreen(dataBuffer);
                    int b = colorModel.getBlue(dataBuffer);
                    int argb = a << 24 | b << 16 | g << 8 | r;
                    backingBuffer.put(argb);
                }
            }

            FontTexture texture = new FontTexture(() ->
                    font.getName().toLowerCase(),
                    image,
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
            );

            texture.upload();
            if (RenderSystem.isOnRenderThread())
            {
                mc.getTextureManager().register(identifier, texture);
            }
            else
            {
                RenderSystem.queueFencedTask(() -> mc.getTextureManager().register(identifier, texture));
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Exists purely because we don't want to use
     * {@link FilterMode#NEAREST} for font rendering.
     */
    private static class FontTexture extends DynamicTexture
    {
        public FontTexture(Supplier<String> label, NativeImage image, GpuSampler sampler)
        {
            super(label, image);
            this.sampler = sampler;
        }
    }
}