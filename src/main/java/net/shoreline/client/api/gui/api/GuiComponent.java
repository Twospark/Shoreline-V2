package net.shoreline.client.api.gui.api;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.Theme;

public interface GuiComponent
{
    String getLabel();

    void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks);

    float getX();

    float getY();

    float getWidth();

    float getHeight();

    void setX(float x);

    void setY(float y);

    void setWidth(float width);

    void setHeight(float height);

    boolean isVisible();

    default Theme getTheme()
    {
        return Theme.getInstance();
    }

    default float getDefaultFeatureHeight()
    {
        return 13f;
    }

    default float getDefaultBorder()
    {
        return 2f;
    }

    default float getDefaultPadding()
    {
        return 1f;
    }
}
