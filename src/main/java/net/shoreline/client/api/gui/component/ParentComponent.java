package net.shoreline.client.api.gui.component;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.render.Render2DUtil;
import net.shoreline.client.impl.render.animation.Animation;
import net.shoreline.client.impl.render.animation.Easing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Setter
@Getter
public class ParentComponent extends AbstractComponent implements Interactable, Globals
{
    protected final List<GuiComponent> components = new ArrayList<>();
    protected final Animation animation = new Animation(200, Easing.LINEAR);
    protected float actualHeight;
    protected boolean open;

    public ParentComponent(String label, Supplier<Boolean> visibility)
    {
        super(label, visibility);
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        drawHoverRect(graphics);
        drawSettingText(graphics, this, getLabel(), false, false);
        drawAnimatedRightText(graphics, open ? "-" : "+", false, partialTicks);
        //drawString(graphics, open ? "-" : "+", getX() + getWidth() - 2, getY() + (getDefaultHeaderHeight() / 2) + 1.0f, false, 255, true);
        if (animation.getFactor() > 0.001)
        {
            drawParentOutline(graphics);
        }
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        scissor(graphics);
        setHeight(getFeatureHeight());
        setActualHeight(getFeatureHeight());

        animation.setState(open);
        animation.setEasing(open ? Easing.CUBIC_OUT : Easing.CUBIC_IN);
        if (animation.getFactor() > 0.001)
        {
            renderComponents(graphics, mouseX, mouseY, partialTicks);
        }

        graphics.disableScissor();
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        if (mouseWithinBounds(mouseX, mouseY, getX(), getY(), getWidth(), getFeatureHeight()) && button == 1)
        {
            setOpen(!open);
        }

        if (open)
        {
            for (GuiComponent component : components)
            {
                if (component instanceof Interactable interactable && component.isVisible())
                {
                    interactable.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button)
    {
        if (open)
        {
            for (GuiComponent component : components)
            {
                if (component instanceof Interactable interactable && component.isVisible())
                {
                    interactable.mouseReleased(mouseX, mouseY, button);
                }
            }
        }
    }

    @Override
    public void mouseScrolled(double x, double y, double scrollX, double scrollY)
    {
        if (open)
        {
            for (GuiComponent component : components)
            {
                if (component instanceof Interactable interactable && component.isVisible())
                {
                    interactable.mouseScrolled(x, y, scrollX, scrollY);
                }
            }
        }
    }

    @Override
    public void keyTyped(int key, int scancode, int modifiers)
    {
        if (open)
        {
            for (GuiComponent component : components)
            {
                if (component instanceof Interactable interactable && component.isVisible())
                {
                    interactable.keyTyped(key, scancode, modifiers);
                }
            }
        }
    }

    @Override
    public void charTyped(char chr)
    {
        if (open)
        {
            for (GuiComponent component : components)
            {
                if (component instanceof Interactable interactable && component.isVisible())
                {
                    interactable.charTyped(chr);
                }
            }
        }
    }

    public void renderComponents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        for (GuiComponent component : components)
        {
            AbstractComponent ac = (AbstractComponent) component;
            if (!checkComponent(ac))
            {
                continue;
            }

            float padding = 0.0f;
            if (modifyParentPadding() && component instanceof ParentComponent)
            {
                padding += getBorder();
            }

            component.setX(getX() + getBorder());
            component.setAlignedX(getX() + (modifyParentPadding() ? 0 : getBorder()));
            component.setWidth(getWidth() - getRightPadding() - padding);
            component.setY(getY() + getYModifier() + getActualHeight());
            component.render(graphics, mouseX, mouseY, partialTicks);
            setActualHeight(getActualHeight() + component.getHeight());
            setHeight((float) (getHeight() + (component.getHeight() * animation.getCurrent())));
        }

        setHeight((float) (getHeight() + (getPadding() * animation.getFactor())));
        setActualHeight(getActualHeight() + getPadding());
    }

    public void scissor(GuiGraphicsExtractor graphics)
    {
        graphics.enableScissor(
                (int) (getX() - 0.5f),
                (int) (getY() + getFeatureHeight() + 0.5f),
                (int) (getX() + getWidth() + 0.5f),
                (int) (getY() + getHeight() + 0.5f));
    }

    public boolean modifyParentPadding()
    {
        return true;
    }

    public float getRightPadding()
    {
        return 0;
    }

    public float getYModifier()
    {
        return 0f;
    }

    protected void drawParentOutline(GuiGraphicsExtractor graphics)
    {
        float outlineY = getY() + getFeatureHeight();
        float outlineHeight = getHeight() - getFeatureHeight() + 0.5f;
        Render2DUtil.drawRect(graphics, getX(), outlineY, getX() + 1.0f, outlineY + outlineHeight, getTheme().getPrimary(0.5f));
    }

    public boolean checkComponent(AbstractComponent component)
    {
        return component.isVisible();
    }
}