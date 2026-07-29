package net.shoreline.client.api.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.shoreline.client.api.gui.api.GuiComponent;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.gui.handler.SearchHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class ShorelineGui extends Screen
{
    protected final List<GuiComponent> components = new ArrayList<>();
    protected final SearchHandler searchHandler = new SearchHandler();

    public ShorelineGui(String type)
    {
        super(Component.literal("Shoreline-" + type));
        load();
        align();
    }

    public abstract void load();

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);
        align();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        searchHandler.render(graphics, partialTicks);
        components.forEach(component -> component.render(graphics, mouseX, mouseY, partialTicks));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        for (GuiComponent component : components)
        {
            if (component instanceof Interactable interactable)
            {
                interactable.mouseClicked(mouseX, mouseY, button);
            }
        }

        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        for (GuiComponent component : components)
        {
            if (component instanceof Interactable interactable)
            {
                interactable.mouseReleased(mouseX, mouseY, button);
            }
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY)
    {
        for (GuiComponent component : components)
        {
            if (component instanceof Interactable interactable)
            {
                interactable.mouseScrolled(x, y, scrollX, scrollY);
            }
        }

        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        int key       = event.key();
        int scancode  = event.scancode();
        int modifiers = event.modifiers();
        for (GuiComponent component : components)
        {
            if (component instanceof Interactable interactable)
            {
                interactable.keyTyped(key, scancode, modifiers);
            }
        }

        searchHandler.onKey(key, scancode, modifiers);
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event)
    {
        char chr = (char) event.codepoint();
        for (GuiComponent component : components)
        {
            if (component instanceof Interactable interactable)
            {
                interactable.charTyped(chr);
            }
        }

        searchHandler.onChar(chr);
        return true;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public void align()
    {
        float sW = minecraft.getWindow().getGuiScaledWidth();
        float x = 4.0f;
        float y = 4.0f;
        for (GuiComponent component : components)
        {
            component.setX(x);
            component.setY(y);
            x += component.getWidth() + 2.0f;
            if (x > sW)
            {
                x = 4;
                y = components.getFirst().getHeight() + 4;
            }
        }
    }
}
