package net.shoreline.client.impl.modules.client;

import lombok.Getter;
import net.shoreline.client.api.element.Element;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.api.setting.Setting;
import net.shoreline.client.api.setting.impl.BooleanSetting;
import net.shoreline.client.api.setting.impl.NumberSetting;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.event.render.HudOverlayEvent;
import net.shoreline.client.impl.gui.hud.EditorScreen;
import net.shoreline.eventbus.api.Subscribe;

@Getter
public class HudModule extends Toggleable
{
    public static HudModule INSTANCE;

    Setting<Boolean> editor = new BooleanSetting.Builder("Editor")
            .setDescription("Opens the clients hud editor")
            .setDefaultValue(false)
            .setObserver(this::openEditor).build();
    Setting<Float> padding = new NumberSetting.Builder<Float>("Padding")
            .setMin(0f).setMax(10f).setDefaultValue(2f)
            .setDescription("The padding between the screen edge and elements")
            .build();

    public HudModule()
    {
        super("HUD", "The clients hud", Category.CLIENT);
        INSTANCE = this;
    }

    @Subscribe
    public void onHudOverlay(HudOverlayEvent.Post event)
    {
        if (mc.screen instanceof EditorScreen
                || mc.options.hideGui
                || mc.gui.getDebugOverlay().showDebugScreen())
        {
            return;
        }

        EditorScreen.runAnchorTick();
        for (Element element : Managers.MODULES.getElements())
        {
            if (element.isEnabled())
            {
                element.draw(event.getGraphics(), event.getPartialTicks());
            }
        }
    }

    public void openEditor(boolean value)
    {
        if (!value)
        {
            return;
        }

        EditorScreen screen = new EditorScreen();
        mc.setScreen(screen);
        editor.setValue(false);
    }
}
