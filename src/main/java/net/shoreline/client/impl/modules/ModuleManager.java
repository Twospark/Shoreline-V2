package net.shoreline.client.impl.modules;

import lombok.Getter;
import net.shoreline.client.api.element.Element;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.registry.OrderedRegistry;
import net.shoreline.client.api.registry.RegistryFeature;
import net.shoreline.client.api.setting.util.SettingContainer;
import net.shoreline.client.impl.event.input.KeyboardEvent;
import net.shoreline.client.impl.modules.client.*;
import net.shoreline.client.impl.modules.hud.WatermarkElement;
import net.shoreline.eventbus.EventBus;
import net.shoreline.eventbus.api.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
public class ModuleManager extends RegistryFeature<Module>
{
    private final List<Element> elements = new ArrayList<>();

    public ModuleManager()
    {
        super("Modules", new OrderedRegistry<>());
        EventBus.getInstance().subscribe(this);

        register(
                new ClickGuiModule(),
                new HudModule(),
                new LatencyModule(),
                new SocialsModule(),
                new ThemeModule(),

                new WatermarkElement()
        );

        getRegistry().getCollection().forEach(SettingContainer::reflectSettings);
    }

    @Subscribe
    public void onKeyboard(KeyboardEvent event)
    {
        if (event.getAction() != 1 || mc.screen != null)
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

    public List<Module> getModules(Predicate<Module> tester)
    {
        List<Module> result = new ArrayList<>();
        for (Module module : getRegistry().getCollection())
        {
            if (tester.test(module))
            {
                result.add(module);
            }
        }

        return result;
    }

    public void register(Module...modules)
    {
        for (Module module : modules)
        {
            if (module instanceof Element element)
            {
                elements.add(element);
            }

            getRegistry().register(module);
        }
    }
}