package net.shoreline.client.api.gui.component;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.GuiComponent;

import java.util.function.Supplier;

@Getter
@Setter
public abstract class AbstractComponent implements GuiComponent
{
    private final String label;
    private final Supplier<Boolean> visibility;
    private float x, y, width, height;

    public AbstractComponent(String label, Supplier<Boolean> visibility)
    {
        this.label = label;
        this.visibility = visibility;
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
        drawComponent(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isVisible()
    {
        return visibility.get();
    }
}
