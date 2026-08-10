package net.shoreline.client.api.element.dynamic;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.element.Element;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.modules.client.HudModule;
import net.shoreline.client.impl.modules.client.ThemeModule;
import net.shoreline.client.impl.render.ColorUtil;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A dynamic implementation of {@link Element}.
 */
@Getter
@Setter
public abstract class DynamicElement extends Element
{
    /** A list of entries. */
    private final List<DynamicEntry> entries = new CopyOnWriteArrayList<>();
    protected float height = 0;
    protected float width  = 0;
    protected boolean left;
    protected boolean top;

    public DynamicElement(String name, String description, float x, float y)
    {
        super(name, description, x, y);
    }

    public DynamicElement(String name, String[] nameAliases, String description, float x, float y)
    {
        super(name, nameAliases, description, x, y);
    }

    /**
     * Use this method to load entries.
     * <p>
     * Called when minecraft is fully done loaded.
     */
    public abstract void loadEntries();

    /**
     * Draws all visible entries and caches this elements width.
     *
     * @param graphics the GuiGraphicsExtractor to render the entries with.
     * @param partialTicks the partialTicks.
     */
    @Override
    public void draw(GuiGraphicsExtractor graphics, float partialTicks)
    {
        drawEntries(graphics, partialTicks);
        cacheWidth();

        float centerW = mc.getWindow().getGuiScaledWidth() / 2f;
        float centerH = mc.getWindow().getGuiScaledHeight() / 2f;
        top  = getY() + (getHeight() / 2.0f) < centerH;
        left = getX() + (getWidth()  / 2.0f) < centerW;
    }

    @Override
    public float getWidth()
    {
        return width;
    }

    @Override
    public float getHeight()
    {
        return height;
    }

    public void drawEntries(GuiGraphicsExtractor graphics, float partialTicks)
    {
        height = 0;
        sortEntries();
        for (DynamicEntry entry : entries)
        {
            if (entry.isDrawing() || !entry.isDone())
            {
                entry.draw(graphics, getX() + (left ? 0 : getWidth()), getY(), height, partialTicks);
            }
        }
    }

    /**
     * Sorts entries, default is length, but you can override and sort differently if needed.
     */
    public void sortEntries()
    {
        getEntries().sort(Comparator.comparingDouble(
                entry -> Managers.TEXT.getWidth(entry.getText().get()) * (top ? -1 : 1)));
    }

    /**
     * The reason why we cache width is that we don't want to calculate
     * it over and over when it will only change once per frame.
     */
    public void cacheWidth()
    {
        int result = 0;
        for (DynamicEntry entry : getEntries())
        {
            if (entry.isDrawing() || !entry.isDone())
            {
                result = Math.max(result, Managers.TEXT.getWidth(entry.getText().get()));
            }
        }

        width = result;
    }

    public void drawTextTransparency(GuiGraphicsExtractor graphics, String text, float x, float y, float transparency)
    {
        drawTextTransparency(graphics, text, x, y, ThemeModule.INSTANCE.getPrimary(), transparency);
    }

    public void drawTextTransparency(GuiGraphicsExtractor graphics, String text, float x, float y, int color, float transparency)
    {
        int alpha = (int) Math.min(transparency * 255, 255);
        int c = ColorUtil.withTransparency(color, alpha).getRGB();
        Managers.TEXT.drawString(graphics, text, x, y, c);
    }
}
