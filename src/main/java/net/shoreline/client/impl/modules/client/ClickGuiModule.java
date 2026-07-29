package net.shoreline.client.impl.modules.client;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.util.Bind;
import net.shoreline.client.impl.gui.click.ClickScreen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ClickGuiModule extends Toggleable
{
    public ClickGuiModule()
    {
        super("ClickGui", "Beautiful shoreline gui", Category.CLIENT);
        this.setBind(Bind.fromKey(GLFW.GLFW_KEY_RIGHT_SHIFT));
    }

    @Override
    protected void onEnable()
    {
        ClickScreen click = new ClickScreen();
        mc.setScreen(click);
    }
}
