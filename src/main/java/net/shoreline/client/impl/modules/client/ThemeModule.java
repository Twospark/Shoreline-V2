package net.shoreline.client.impl.modules.client;

import lombok.Getter;
import net.shoreline.client.api.gui.Theme;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.ColorSetting;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.eventbus.api.Subscribe;

import java.awt.*;

@Getter
public class ThemeModule extends Module
{
    public static ThemeModule INSTANCE;

    Setting<Color> primary = new ColorSetting.Builder("Primary")
            .setRgb(0xFF5F5FDE)
            .setObserver(v -> Theme.getInstance().update(v.getRGB()))
            .setDescription("The primary color of the client")
            .build();

    public ThemeModule()
    {
        super("Theme", "The clients theme colors", Category.CLIENT);
        INSTANCE = this;
    }

    @Subscribe(priority = -1000)
    public void onLoaded(ClientEvent.Loaded event)
    {
        Theme.getInstance().update(primary.getValue().getRGB());
    }

    public int getPrimary()
    {
        return primary.getValue().getRGB();
    }
}