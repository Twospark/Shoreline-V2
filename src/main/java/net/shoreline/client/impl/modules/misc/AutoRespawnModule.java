package net.shoreline.client.impl.modules.misc;

import net.minecraft.client.gui.screens.DeathScreen;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.render.ScreenEvent;
import net.shoreline.eventbus.api.Subscribe;

public class AutoRespawnModule extends Toggleable
{
    private boolean respawn;

    public AutoRespawnModule()
    {
        super("AutoRespawn", "Respawns immediately after death", Category.MISCELLANEOUS);
    }

    @Subscribe
    public void onTick(TickEvent event)
    {
        if (!checkNull() && respawn && mc.player.isDeadOrDying())
        {
            mc.player.respawn();
            respawn = false;
        }
    }

    @Subscribe
    public void onOpenScreen(ScreenEvent event)
    {
        if (event.getScreen() instanceof DeathScreen)
        {
            respawn = true;
        }
    }
}