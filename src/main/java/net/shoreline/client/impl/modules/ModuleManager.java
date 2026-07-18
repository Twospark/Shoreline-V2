package net.shoreline.client.impl.modules;

import net.shoreline.client.Shoreline;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.registry.OrderedRegistry;
import net.shoreline.client.api.registry.RegistryFeature;
import net.shoreline.client.impl.event.input.KeyboardEvent;
import net.shoreline.client.impl.modules.client.ClickGui;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;

public class ModuleManager extends RegistryFeature<Module>
{
    public ModuleManager()
    {
        super("Modules", new OrderedRegistry<>());
        EventBus.getInstance().subscribe(this);

        getRegistry().register(
                new ClickGui()
        );
    }

    @Subscribe
    public void onKeyboard(KeyboardEvent event)
    {
        if (event.getAction() != 1)
        {
            return;
        }

        for (Module module : getRegistry().getCollection())
        {
            if (!(module instanceof Toggleable toggleable))
            {
                continue;
            }

            if (toggleable.getBind().getKey() == event.getKey())
            {
                toggleable.toggle();
            }
        }
    }
}