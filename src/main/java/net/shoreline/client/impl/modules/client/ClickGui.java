package net.shoreline.client.impl.modules.client;

import net.shoreline.client.api.gui.Theme;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.ColorSetting;
import net.shoreline.client.api.setting.util.Bind;
import net.shoreline.client.impl.gui.click.ClickScreen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ClickGui extends Toggleable
{
    private final Setting<Color> color = new ColorSetting.Builder("Color")
            .setRgb(0xFFFF5050)
            .setObserver(v -> Theme.getInstance().update(v.getRGB()))
            .setDescription("The color of the gui").build();

    public ClickGui()
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
