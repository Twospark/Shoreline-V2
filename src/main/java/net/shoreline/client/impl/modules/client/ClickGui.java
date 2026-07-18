package net.shoreline.client.impl.modules.client;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.util.Bind;
import org.lwjgl.glfw.GLFW;

public class ClickGui extends Toggleable
{
    public ClickGui()
    {
        super("ClickGui", "Beautiful shoreline gui", Category.CLIENT);
        this.setBind(Bind.fromKey(GLFW.GLFW_KEY_RIGHT_SHIFT));
    }
}
