package net.shoreline.client.impl;

import lombok.experimental.UtilityClass;
import net.shoreline.client.impl.config.ConfigManager;
import net.shoreline.client.impl.event.ClientEvent;
import net.shoreline.client.impl.modules.ModuleManager;
import net.shoreline.client.impl.network.NetworkManager;
import net.shoreline.client.impl.render.TextManager;
import net.shoreline.eventbus.EventBus;

@UtilityClass
public class Managers
{
    public static NetworkManager NETWORK;
    public static ConfigManager CONFIG;
    public static ModuleManager MODULES;
    public static TextManager TEXT;

    public void init()
    {
        NETWORK = new NetworkManager();
        MODULES = new ModuleManager();
        TEXT    = new TextManager();
        CONFIG  = new ConfigManager(); // configs last.

        EventBus.getInstance().post(new ClientEvent.Loaded());
    }
}
