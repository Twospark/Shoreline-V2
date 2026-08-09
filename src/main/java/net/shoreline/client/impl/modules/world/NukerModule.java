package net.shoreline.client.impl.modules.world;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;

public class NukerModule extends Toggleable
{
    public NukerModule()
    {
        super("Nuker", "Clears nearby blocks", Category.WORLD);
    }
}
