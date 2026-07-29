package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.Interactable;

import java.util.function.Supplier;

public class RunnableComponent extends AbstractComponent implements Interactable
{
    private final Runnable runnable;

    public RunnableComponent(String label, Supplier<Boolean> visibility, Runnable runnable)
    {
        super(label, visibility);
        this.runnable = runnable;
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        drawHoverRect(graphics);
        drawSettingText(graphics, this, getLabel(), false, false);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        setHeight(getFeatureHeight());
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isHovered(mouseX, mouseY) && button == 0)
        {
            runnable.run();
        }
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
}
