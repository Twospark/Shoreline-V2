package net.shoreline.client.impl.render;

import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class ColorUtil
{
    public Color interpolate(Color start, Color end, double factor)
    {
        return new Color(
                (int) (start.getRed() + (end.getRed() - start.getRed()) * factor),
                (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * factor),
                (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * factor),
                (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * factor));
    }

    public Color withTransparency(Color color, int alpha)
    {
        return new Color(color.getRed(),
                color.getGreen(),
                color.getBlue(),
                alpha);
    }

    public Color withTransparency(int color, int alpha)
    {
        return withTransparency(new Color(color, true), alpha);
    }

    public Color withTransparency(int color, float alpha)
    {
        return withTransparency(color, (int) Math.clamp(alpha * 255, 0, 255));
    }

    public int getSimpleVariation(float offset, Color color)
    {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.HSBtoRGB(hsb[0], hsb[1], ColorUtil.getVariation(1.0f, 1.5f, hsb[2], (int) (-offset)));
    }

    public float getVariation(float speedFactor, float rangeFactor, float value, int offset)
    {
        float time = (float) Math.sin(getRainbowHue(speedFactor / 36, (int) (offset / speedFactor)) * 360);
        float variation = time * (rangeFactor / 8);
        return Math.clamp(value - (rangeFactor / 8) + variation, 0f, 1f);
    }

    public float getRainbowHue(float speedFactor, int offset)
    {
        float speed = 2500 / speedFactor;
        return ((System.currentTimeMillis() + offset) % (int) speed) / speed;
    }
}
