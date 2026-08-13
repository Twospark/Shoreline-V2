package net.shoreline.client.impl.modules.client;

import lombok.Getter;
import net.shoreline.client.api.gui.Theme;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Concurrent;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.ColorSetting;
import net.shoreline.client.api.thread.AsyncFeature;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.eventbus.api.Subscribe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ThemeModule extends Concurrent
{
    public static ThemeModule INSTANCE;
    private static final AsyncFeature<?> async = new AsyncFeature<>("Shoreline-Color");

    Setting<Color> primarySetting = new ColorSetting.Builder("Primary")
            .setRgb(0xFF5F5FDE)
            .setObserver(v -> Theme.getInstance().update(v.getRGB()))
            .setDescription("The primary color of the client")
            .build();

    private final List<ColorSetting> allColorSettings = new ArrayList<>();

    public ThemeModule()
    {
        super("Theme", "The clients theme colors", Category.CLIENT);
        INSTANCE = this;
    }

    @Subscribe(priority = -1000)
    public void onLoaded(ClientEvent.Loaded event)
    {
        Theme.getInstance().update(primarySetting.getValue().getRGB());
        for (Module module : Managers.MODULES.getRegistry().getCollection())
        {
            if (module == this)
            {
                continue;
            }

            for (Setting<?> setting : module.getSettings())
            {
                if (!(setting instanceof ColorSetting colorSetting))
                {
                    continue;
                }

                allColorSettings.add(colorSetting);
            }
        }
    }

    @Subscribe
    public void onTick(TickEvent event)
    {
        async.submit(() ->
        {
            for (ColorSetting setting : allColorSettings)
            {
                if (!setting.isGlobal())
                {
                    continue;
                }

                setting.setValueAlpha(primarySetting.getValue());
            }
        });
    }

    public int getPrimary()
    {
        return primarySetting.getValue().getRGB();
    }
}