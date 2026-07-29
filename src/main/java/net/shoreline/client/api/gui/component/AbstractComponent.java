package net.shoreline.client.api.gui.component;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.render.ColorUtil;
import net.shoreline.client.impl.render.Render2DUtil;
import net.shoreline.client.impl.render.animation.Animation;
import net.shoreline.client.impl.render.animation.Easing;
import net.shoreline.client.impl.render.animation.Smoother;

import java.awt.*;
import java.util.function.Supplier;

@Getter
@Setter
public abstract class AbstractComponent implements GuiComponent, Globals
{
    protected final String label;
    protected final Supplier<Boolean> visibility;
    protected float x, y, width, height, alignedX;
    protected final Animation scrollAnimation;
    protected final Animation hoverAnimation;
    protected final Smoother textSmoother;
    protected boolean scrollState;

    public AbstractComponent(String label, Supplier<Boolean> visibility)
    {
        this.label = label;
        this.visibility = visibility;
        this.hoverAnimation = new Animation(150, Easing.SMOOTH);
        this.scrollAnimation = new Animation(false, 0, 0, 1000, Easing.LINEAR);
        this.textSmoother = new Smoother();
    }

    /**
     * Its important that we do all the actual rendering
     * in this method and not the render method so we
     * don't have to draw components that are off-screen,
     * while still getting their correct dimensions.
     *
     * @param graphics the graphics provided.
     * @param mouseX the mouse x position.
     * @param mouseY the mouse y position.
     * @param partialTicks the current progress between ticks.
     */
    public abstract void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks);

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        boolean hovered = mouseWithinBounds(mouseX, mouseY, getAlignedX(), getY(), getWidth(), getFeatureHeight());
        if (hovered)
        {
            if (scrollAnimation.isFinished())
            {
                scrollState = !scrollState;
                scrollAnimation.setState(scrollState);
            }
        }
        else
        {
            scrollAnimation.setState(false);
        }

        hoverAnimation.setState(hovered);
        if (shouldRenderComponent())
        {
            drawComponent(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void setScroll(double scroll)
    {
        scrollAnimation.setTarget(scroll);
    }

    @Override
    public boolean isVisible()
    {
        return visibility == null || visibility.get();
    }

    protected void drawHoverRect(GuiGraphicsExtractor graphics)
    {
        Render2DUtil.drawRect(graphics, getX(), getY() + 1.5f, getX() + getWidth(), getY() + getFeatureHeight(), ColorUtil.withTransparency(Color.GRAY, Math.max(50, (int) (75 * hoverAnimation.getFactor()))).getRGB());
    }

    public void drawValueComponent(GuiGraphicsExtractor graphics, String value, float partialTicks)
    {
        drawHoverRect(graphics);
        scissorText(graphics, value);
        drawSettingText(graphics, this, getLabel(), false, false);
        graphics.disableScissor();
        drawAnimatedRightText(graphics, value, false, partialTicks);
    }

    public void scissorText(GuiGraphicsExtractor graphics, String value)
    {
        float align = getWidth() - getTextPadding();
        float nameX = getX() + getTextPadding();
        float x = getX() + align;
        float y = getY() + (getFeatureHeight()) / 2 + 1f;

        graphics.enableScissor((int) nameX, (int) (y - Managers.TEXT.getHeight()), (int) (x - Managers.TEXT.getWidth(value) - 2.5f), (int) (y + Managers.TEXT.getHeight()));
        float maxWidth = x - nameX - Managers.TEXT.getWidth(value) - 5.0f;
        float overflow = Managers.TEXT.getWidth(getLabel()) - maxWidth;
        setScroll(Math.max(0, overflow));
    }

    public void drawAnimatedRightText(GuiGraphicsExtractor graphics, String text, boolean primaryColor, float partialTicks)
    {
        drawRightSettingText(graphics, this, text, primaryColor, (float) textSmoother.smooth(Managers.TEXT.getWidth(text), 0.5f, partialTicks));
    }

    public boolean shouldRenderComponent()
    {
        return getY() + getHeight() > 0 && getY() < mc.getWindow().getGuiScaledHeight();
    }
}
