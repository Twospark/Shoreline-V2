package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.gui.handler.DragHandler;
import net.shoreline.client.api.gui.handler.ScrollHandler;
import net.shoreline.client.api.gui.handler.SearchHandler;
import net.shoreline.client.impl.render.Render2DUtil;

public class WindowComponent extends ParentComponent
{
    protected final float padding = 50;
    protected final SearchHandler search;
    protected final ScrollHandler scroll;
    protected DragHandler dragHandler;

    public WindowComponent(String label, SearchHandler searchHandler)
    {
        super(label, () -> true);
        setWidth(110);
        setOpen(true);

        scroll = new ScrollHandler(this, padding);
        search = searchHandler;
        animation.setStateHard(true);
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        Render2DUtil.drawRect(graphics, getX(), getY(), getX() + getWidth(), getY() + getFeatureHeight(), 0x00000046);
        Render2DUtil.drawRect(graphics, getX(), getY(), getX() + getWidth(), getY() + getFeatureHeight(), getTheme().getPrimary(0.5f));
        if (isOpen())
        {
            Render2DUtil.drawRect(graphics, getX(), getY() + getFeatureHeight(), getX() + getWidth(), getY() + getMaxHeight(), 0x60000000);
        }

        Render2DUtil.drawBorderedRect(graphics, getX() - 0.5f, getY() - 0.5f, getX() + getWidth() + 0.5f, getY() + getMaxHeight() + 0.5f, 0.5f, 0, getTheme().getPrimary());
        drawString(graphics, getLabel(), getX() + 4f, getY() + (getFeatureHeight() / 2), false, false);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (dragHandler == null)
        {
            this.dragHandler = new DragHandler(getX(), getY());
        }

        scroll.handleRender(mouseX, mouseY, partialTicks);
        dragHandler.handleRender(mouseX, mouseY);
        if (dragHandler.isDragging())
        {
            setX(dragHandler.getX());
            setY(dragHandler.getY());
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        if (mouseWithinBounds(mouseX, mouseY, getX(), getY(), getWidth(), getFeatureHeight()) && button == 1)
        {
            setOpen(!open);
        }

        if (isOpen())
        {
            for (GuiComponent component : components)
            {
                if (!(component instanceof Interactable toggleable))
                {
                    continue;
                }

                if (component instanceof AbstractComponent abstractComponent && !checkComponent(abstractComponent))
                {
                    continue;
                }

                if (component.isVisible() && component.getY() + 1 > getY() + getFeatureHeight())
                {
                    toggleable.mouseClicked(mouseX, mouseY, button);
                }
            }
        }

        if (dragHandler == null)
        {
            this.dragHandler = new DragHandler(getX(), getY());
        }

        dragHandler.handleMouseClicked((float) mouseX, (float) mouseY, button,
                mouseWithinBounds(
                        mouseX,
                        mouseY,
                        getX(),
                        getY(),
                        getWidth(),
                        getFeatureHeight()));
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button)
    {
        super.mouseReleased(mouseX, mouseY, button);
        if (dragHandler == null)
        {
            dragHandler = new DragHandler(getX(), getY());
        }

        dragHandler.setDragging(false);
    }

    @Override
    public void mouseScrolled(double x, double y, double scrollX, double scrollY)
    {
        scroll.mouseScrolled(scrollY);
    }

    @Override
    public float getFeatureHeight()
    {
        return 14f;
    }

    @Override
    public float getPadding()
    {
        return 1.5f - Math.min(scroll.getScroll(), 0);
    }

    @Override
    public float getBorder()
    {
        return 1f;
    }

    @Override
    public float getRightPadding()
    {
        return 1f;
    }

    @Override
    public float getYModifier()
    {
        return -scroll.getScroll();
    }

    @Override
    public boolean checkComponent(AbstractComponent component)
    {
        if (search.isSearching())
        {
            if (!component.getLabel().toLowerCase().startsWith(search.getSearch().toLowerCase()))
            {
                return false;
            }

            if (!search.getSearch().isEmpty())
            {
                search.setCompletion(component.getLabel());
            }
        }

        return super.checkComponent(component);
    }

    public boolean isOpen()
    {
        return animation.getFactor() > 0.001;
    }

    public float getMaxHeight()
    {
        return Math.min(getHeight(), mc.getWindow().getGuiScaledHeight() - padding);
    }
}
