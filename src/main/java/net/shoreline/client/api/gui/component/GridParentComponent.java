package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.GuiComponent;

import java.util.function.Supplier;

public class GridParentComponent extends ParentComponent
{
    private final int columns;

    public GridParentComponent(String label, Supplier<Boolean> visibility, int columns)
    {
        super(label, visibility);
        this.columns = columns;
    }

    @Override
    public void renderComponents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        float contentWidth = getWidth() - getRightPadding();
        float columnWidth = contentWidth / columns;

        int column = 0;
        float curr = 0.0f;
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

            component.setX(getX() + getBorder() + (column * columnWidth));
            component.setAlignedX(getX() + (modifyParentPadding() ? 0 : getBorder()) + (column * columnWidth));
            component.setWidth(columnWidth - getBorder() - padding + 1);
            component.setY(getY() + getYModifier() + getActualHeight());
            component.render(graphics, mouseX, mouseY, partialTicks);

            curr = Math.max(curr, component.getHeight());
            column++;

            if (column >= columns)
            {
                finishCurrentRow(curr);
                column = 0;
                curr = 0.0f;
            }
        }

        if (column > 0)
        {
            finishCurrentRow(curr);
        }

        setActualHeight(getActualHeight() + getPadding());
        setHeight((float) (getHeight() + (getPadding() * animation.getCurrent())));
    }

    private void finishCurrentRow(float current)
    {
        setActualHeight(getActualHeight() + current);
        setHeight((float) (getHeight() + (current * animation.getCurrent())));
    }
}
