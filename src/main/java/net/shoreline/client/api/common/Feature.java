package net.shoreline.client.api.common;

import net.shoreline.client.api.interfaces.Globals;
import net.shoreline.client.api.interfaces.Identifiable;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Listener;

public class Feature implements Identifiable, Globals
{
    private final String name;
    private final String[] nameAliases;

    public Feature(String name)
    {
        this(name, new String[0]);
    }

    public Feature(String name, String[] nameAliases)
    {
        this.name = name;
        this.nameAliases = nameAliases;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String[] getAliases()
    {
        return nameAliases;
    }

    public void registerListener(Listener<?> listener)
    {
        registerListener(listener, false);
    }

    public void registerListener(Listener<?> listener, boolean always)
    {
        if (always)
        {
            EventBus.getInstance().register(listener);
        }
        else
        {
            EventBus.getInstance().register(this, listener);
        }
    }

    protected void runOnThread(Runnable runnable)
    {
        if (mc.isSameThread())
        {
            runnable.run();
        }
        else
        {
            mc.execute(runnable);
        }
    }

    protected boolean checkNull()
    {
        return mc.player == null || mc.level == null;
    }
}
