package net.shoreline.client.impl.modules.hud;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.element.dynamic.DynamicElement;
import net.shoreline.client.api.element.dynamic.DynamicEntry;
import net.shoreline.client.util.math.PerSecond;

public class FpsElement extends DynamicElement
{
    private final PerSecond fps = new PerSecond();

    public FpsElement()
    {
        super("FPS", "Displays current game FPS", 200, 200);
    }

    @Override
    public void loadEntries()
    {
        getEntries().add(new DynamicEntry(this, this::getFPSText, () -> true));
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, float partialTicks)
    {
        super.draw(graphics, partialTicks);
        fps.count();
    }

    public String getFPSText()
    {
        return "FPS " + ChatFormatting.WHITE + fps.getPerSecond();
    }
}
