package net.shoreline.client.impl.modules.render;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;

public class ChamsModule extends Toggleable
{
    public ChamsModule()
    {
        super("Chams", "Renders a cham over entities", Category.RENDER);
    }
}
