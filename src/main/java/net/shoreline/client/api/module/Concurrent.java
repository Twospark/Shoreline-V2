package net.shoreline.client.api.module;

import net.shoreline.eventbus.EventBus;

public class Concurrent extends Module
{
    public Concurrent(String name, String description, Category category)
    {
        super(name, description, category);
        EventBus.getInstance().subscribe(this);
    }

    public Concurrent(String name, String[] nameAliases, String description, Category category)
    {
        super(name, nameAliases, description, category);
        EventBus.getInstance().subscribe(this);
    }
}
