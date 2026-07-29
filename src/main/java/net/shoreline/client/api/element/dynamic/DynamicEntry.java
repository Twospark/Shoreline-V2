package net.shoreline.client.api.element.dynamic;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.render.animation.Animation;
import net.shoreline.client.impl.render.animation.Easing;
import net.shoreline.client.impl.render.animation.UnboundAnimation;

import java.util.function.Supplier;

/**
 * A dynamic element entry.
 */
@Getter
@Setter
public class DynamicEntry
{
    private final DynamicElement element;
    private final Supplier<String> text;
    private final UnboundAnimation animation;
    public final Animation yAnimation;

    private Supplier<Boolean> drawing;
    private boolean lastState;
    private float lastWidth;

    public DynamicEntry(DynamicElement element, Supplier<String> text, Supplier<Boolean> drawing)
    {
        this.element = element;
        this.text = text;
        this.drawing = drawing;
        this.lastState = drawing.get();
        this.animation = new UnboundAnimation(300, Easing.EXPO_OUT);
        this.yAnimation = new Animation(false, 0, 1, 150, Easing.LINEAR);
    }

    public void draw(GuiGraphicsExtractor graphics, float x, float y, float currentOffset, float delta)
    {
        boolean drawing = this.drawing.get();
        if (!drawing && isDone())
        {
            return;
        }

        updateHeight(currentOffset);
        String current = text.get();

        boolean left  = getElement().isLeft();
        float width   = Managers.TEXT.getWidth(current);
        float renderX = x - (left ? width : 0);
        float renderY = y + currentOffset;

        if (drawing)
        {
            width = left ? width : -width;
            animation.setEasing(width > lastWidth ? Easing.SMOOTH : Easing.EXPO_OUT);
            lastWidth = width;

            renderX += animation.get(width);
            yAnimation.setState(true);
        }
        else
        {
            if (!isDone())
            {
                animation.setEasing(Easing.EXPO_IN);
                renderX += animation.get(left ? -width + width - 2.0f : 2.0f);
                if (animation.getFactor() > 0.1)
                {
                    yAnimation.setState(false);
                }
            }
        }

        drawText(graphics, current, renderX, renderY);
    }

    public void updateHeight(float offset)
    {
        float textHeight = Managers.TEXT.getHeight() + 1;
        float animatedHeight = (float) (textHeight * yAnimation.getFactor());
        getElement().setHeight(offset + animatedHeight);
    }

    public float getHeight()
    {
        float textHeight = Managers.TEXT.getHeight() + 1;
        return (float) (textHeight * (1.0f - yAnimation.getFactor()));
    }

    /**
     * We make this a separate method so if any hud entries need custom
     * colors (like potion hud). they can just override this.
     */
    public void drawText(GuiGraphicsExtractor graphics, String string, float x, float y)
    {
        getElement().drawTextTransparency(graphics, string, x, y, (float) yAnimation.getFactor());
    }

    public boolean isDrawing()
    {
        return drawing.get();
    }

    public boolean isDone()
    {
        return yAnimation.getFactor() < 0.001;
    }
}