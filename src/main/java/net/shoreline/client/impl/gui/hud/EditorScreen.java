package net.shoreline.client.impl.gui.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.shoreline.client.api.element.Anchor;
import net.shoreline.client.api.element.Element;
import net.shoreline.client.api.gui.ShorelineGui;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.component.WindowComponent;
import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.impl.Managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EditorScreen extends ShorelineGui implements Globals
{
    public EditorScreen()
    {
        super("HUD");
    }

    @Override
    public void load()
    {
        WindowComponent component = new WindowComponent("Elements", searchHandler, 1);
        for (Element element : Managers.MODULES.getElements())
        {
            GuiComponent eComponent = element.getComponent();
            if (eComponent != null)
            {
                component.getComponents().add(eComponent);
            }
        }

        components.add(component);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        List<Element> sorted = new ArrayList<>();
        for (Element element : Managers.MODULES.getElements())
        {
            if (element.isEnabled())
            {
                sorted.add(element);
            }
        }

        runAnchorTick();
        sorted.sort(Comparator.comparing(Element::getIndex));
        for (Element element : sorted)
        {
            element.drawGui(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        float mouseX = (float) event.x();
        float mouseY = (float) event.y();
        for (Element element : Managers.MODULES.getElements())
        {
            element.mouseClicked(mouseX, mouseY, event.button());
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        for (Element element : Managers.MODULES.getElements())
        {
            if (element.isEnabled())
            {
                element.mouseReleased();
            }
        }

        return super.mouseReleased(event);
    }

    public static void runAnchorTick()
    {
        for (Anchor anchor : Anchor.values())
        {
            if (anchor == Anchor.NONE)
            {
                continue;
            }

            float offset = 2f;
            List<Element> anchoredElements = Managers.MODULES.getElements().stream()
                    .filter(e -> e.getAnchor() == anchor)
                    .sorted(Comparator.comparingInt(e -> e.getIndex()))
                    .toList();

            if (anchoredElements.isEmpty())
            {
                continue;
            }

            int i = 0;
            float sW = mc.getWindow().getGuiScaledWidth();
            float sH = mc.getWindow().getGuiScaledHeight();
            float currentY = anchor.getY(sH, 0, offset);
            for (Element element : anchoredElements)
            {
                if (element.getDrag().isDragging())
                {
                    continue;
                }

                element.setIndex(i);
                i++;

                if (!element.isEnabled())
                {
                    continue;
                }

                element.setX(anchor.getX(sW, element.getWidth()));
                switch (anchor)
                {
                    case TOP_LEFT:
                    case TOP_RIGHT:
                        element.setY(currentY);
                        currentY += element.getHeight();
                        break;
                    case BOTTOM_LEFT:
                    case BOTTOM_RIGHT:
                        element.setY(currentY -= element.getHeight());
                        break;
                }
            }
        }
    }
}
