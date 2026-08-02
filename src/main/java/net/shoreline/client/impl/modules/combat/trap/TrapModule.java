package net.shoreline.client.impl.modules.combat.trap;

import net.shoreline.client.api.module.Category;
import net.shoreline.client.impl.modules.impl.ObsidianPlacerModule;

import java.util.EnumSet;

public abstract class TrapModule extends ObsidianPlacerModule
{
    protected final TrapPositionCalc trapPos = new TrapPositionCalc();

    public TrapModule(String name, String description, Category category)
    {
        super(name, description, category);
    }

    public TrapModule(String name, String[] nameAliases, String description, Category category)
    {
        super(name, nameAliases, description, category);
    }

    public abstract EnumSet<TrapLayer> getLayers();
}