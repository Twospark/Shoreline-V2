package net.shoreline.client.api.gui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.shoreline.client.api.gui.api.Interactable;
import net.shoreline.client.api.setting.impl.BindSetting;
import net.shoreline.client.api.setting.util.Bind;
import org.lwjgl.glfw.GLFW;

public class BindComponent extends AbstractComponent implements Interactable
{
    private final BindSetting setting;
    private boolean listening = false;

    public BindComponent(BindSetting setting)
    {
        super(setting.getName(), setting.getVisible());
        this.setting = setting;
    }

    @Override
    public void drawComponent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
    {
        Bind bind = setting.getValue();
        String value = listening
                ? "..."
                : bind.toString().toUpperCase();

        drawValueComponent(graphics, value, partialTicks);
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
        if (listening)
        {
            if (button != GLFW.GLFW_MOUSE_BUTTON_2 && button != GLFW.GLFW_MOUSE_BUTTON_1)
            {
                setting.setValue(new Bind(GLFW.GLFW_KEY_LAST + button));
                listening = false;
            }
        }

        if (isHovered(mouseX, mouseY))
        {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1)
            {
                listening = !listening;
            }
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
        if (!listening)
        {
            return;
        }

        switch (key)
        {
            case GLFW.GLFW_KEY_DELETE:
            case GLFW.GLFW_KEY_BACKSPACE:
            case GLFW.GLFW_KEY_ESCAPE:
                setting.setValue(Bind.none());
                break;
            default:
                setting.setValue(new Bind(key));
                break;
        }

        listening = false;
    }

    @Override
    public void charTyped(char chr)
    {
    }
}
