package net.shoreline.client.impl.modules;

import lombok.Getter;
import net.shoreline.client.api.element.Element;
import net.shoreline.client.api.element.dynamic.DynamicElement;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.registry.OrderedRegistry;
import net.shoreline.client.api.registry.RegistryFeature;
import net.shoreline.client.api.setting.util.SettingContainer;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.client.impl.event.input.KeyboardEvent;
import net.shoreline.client.impl.modules.client.*;
import net.shoreline.client.impl.modules.combat.*;
import net.shoreline.client.impl.modules.hud.ModulesElement;
import net.shoreline.client.impl.modules.hud.WatermarkElement;
import net.shoreline.client.impl.modules.misc.FakePlayerModule;
import net.shoreline.client.impl.modules.movement.SprintModule;
import net.shoreline.client.impl.modules.world.AirPlaceModule;
import net.shoreline.client.impl.modules.world.AutoToolModule;
import net.shoreline.client.impl.modules.world.SpeedMineModule;
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
                new InteractionsModule(),
                new InventoryModule(),
                new LatencyModule(),
                new RotationsModule(),
                new SocialsModule(),
                new ThemeModule(),

                new AuraModule(),
                new AutoCrystalModule(),
                new AutoMineModule(),
                new AutoTotemModule(),
                new FeetTrapModule(),
                new OffhandGappleModule(),

                new FakePlayerModule(),

                new SprintModule(),

                new AirPlaceModule(),
                new AutoToolModule(),
                new SpeedMineModule(),

                new ModulesElement(),
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

    @Subscribe
    public void onLoaded(ClientEvent.McLoaded event)
    {
        for (Element element : getElements())
        {
            if (!(element instanceof DynamicElement dynamicElement))
            {
                continue;
            }

            dynamicElement.loadEntries();
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