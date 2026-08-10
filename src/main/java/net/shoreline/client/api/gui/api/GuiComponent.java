package net.shoreline.client.api.gui.api;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.Theme;
import net.shoreline.client.api.gui.component.AbstractComponent;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.render.ColorUtil;

import java.awt.*;

public interface GuiComponent
{
    String getLabel();

    void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks);

    float getX();

    float getAlignedX();

    float getY();

    float getWidth();

    float getHeight();

    void setX(float x);

    void setAlignedX(float x);

    void setY(float y);

    void setWidth(float width);

    void setHeight(float height);

    void setScroll(double scroll);

    boolean isVisible();

    default Theme getTheme()
    {
        return Theme.getInstance();
    }

    /**
     * Component constants.
     * Could just make all of these settings instead,
     * but I don't like the guis people make.
     */
    default float getFeatureHeight()
    {
        return 15f;
    }

    default float getBorder()
    {
        return 2f;
    }

    default float getPadding()
    {
        return 1f;
    }

    default float getTextPadding()
    {
        return 2f;
    }

    /* ---------- End of component constants ---------- */

    default boolean isHovered(double mouseX, double mouseY)
    {
        return mouseWithinBounds(mouseX,
                mouseY,
                getAlignedX(),
                getY(),
                getWidth(),
                getHeight());
    }

    default boolean mouseWithinBounds(double mouseX,
                                      double mouseY,
                                      double x,
                                      double y,
                                      double width,
                                      double height)
    {
        return (mouseX >= x && mouseX <= (x + width)) &&
                (mouseY >= y && mouseY <= (y + height));
    }

    default void drawString(GuiGraphicsExtractor graphics,
                            String text,
                            float x,
                            float y,
                            boolean primaryColor,
                            boolean rightAlign)
    {
        int color = primaryColor
                ? getTheme().getPrimary()
                : 0xFFFFFFFF;
        float align = rightAlign
                ? x - Managers.TEXT.getWidth(text)
                : x;

        Managers.TEXT.drawString(graphics,
                text,
                align,
                y - (Managers.TEXT.getHeight() >> 1),
                color);
    }

    default void drawRightString(GuiGraphicsExtractor graphics,
                                 String text,
                                 float x,
                                 float y,
                                 boolean primaryColor,
                                 float width)
    {
        int color = primaryColor
                ? getTheme().getPrimary()
                : 0xFFFFFFFF;
        float align = x - width;

        Managers.TEXT.drawString(graphics,
                text,
                align,
                y - (Managers.TEXT.getHeight() >> 1),
                color);
    }

    default void drawString(GuiGraphicsExtractor graphics,
                            String text,
                            float x,
                            float y,
                            int color)
    {
        Managers.TEXT.drawString(graphics,
                text,
                x,
                y - (Managers.TEXT.getHeight() >> 1),
                color);
    }

    default void drawRightSettingText(GuiGraphicsExtractor graphics,
                                      String value,
                                      boolean primaryColor,
                                      float width)
    {
        if (value == null)
        {
            return;
        }

        float align = getWidth() - getTextPadding();
        float x = getAlignedX() + align;
        float y = getY() + (getFeatureHeight()) / 2 + 1f;
        drawRightString(graphics, value, x, y, primaryColor, width);
    }

    default void drawSettingText(GuiGraphicsExtractor graphics,
                                 GuiComponent component,
                                 String value,
                                 boolean primaryColor,
                                 boolean rightAlign)
    {
        float extra = 0.f;
        if (component instanceof AbstractComponent ac && !rightAlign)
        {
            extra  = (float) ac.getHoverAnimation().getCurrent();
            extra -= (float) ac.getScrollAnimation().getCurrent();
        }

        float align = rightAlign ? component.getWidth() - getTextPadding() : getTextPadding();
        float x = component.getX() + align + extra;
        float y = component.getY() + (getFeatureHeight()) / 2 + 1f;

        drawString(graphics, value, x, y, primaryColor, rightAlign);
    }
}
