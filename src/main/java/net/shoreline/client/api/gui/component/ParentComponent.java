package net.shoreline.client.api.gui.component;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.render.Render2DUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ParentComponent extends AbstractComponent implements Interactable, Globals
{
    @Getter
    private final List<GuiComponent> components = new ArrayList<>();
    private boolean open;

    public ParentComponent(String label, Supplier<Boolean> visibility)
    {
        super(label, visibility);
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        int color = getTheme().getPrimary();
        Render2DUtil.drawRect(graphics, getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
        graphics.text(mc.font, getLabel(), (int) (getX() + 4), (int) (getY() + (getHeight() / 2f)), 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        setHeight(getDefaultFeatureHeight());

        if (open)
        {
            for (GuiComponent component : components)
            {
                if (!component.isVisible())
                {
                    continue;
                }

                component.setX(getX() + getLeftPadding());
                component.setWidth(getWidth() - getRightPadding());
                component.setY(getY() + getYModifier() + getHeight());
                component.render(graphics, mouseX, mouseY, partialTicks);
                setHeight(getHeight() + component.getHeight());
            }

            setHeight(getHeight() + getPadding());
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {

    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button)
    {

    }

    @Override
    public void mouseScrolled(double x, double y, double scrollX, double scrollY)
    {

    }

    @Override
    public void keyTyped(int key, int scancode, int modifiers)
    {

    }

    @Override
    public void charTyped(char chr)
    {

    }

    public float getFeatureHeight()
    {
        return getDefaultFeatureHeight();
    }

    public float getPadding()
    {
        return getDefaultPadding();
    }

    public float getLeftPadding()
    {
        return getDefaultBorder();
    }

    public float getRightPadding()
    {
        return 0;
    }

    public float getYModifier()
    {
        return 0f;
    }
}