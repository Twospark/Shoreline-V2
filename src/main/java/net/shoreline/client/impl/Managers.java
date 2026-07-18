package net.shoreline.client.impl;

import lombok.experimental.UtilityClass;
import net.shoreline.client.impl.config.ConfigManager;
import net.shoreline.client.impl.modules.ModuleManager;
import net.shoreline.client.impl.network.NetworkManager;

@UtilityClass
public class Managers
{
    public static NetworkManager NETWORK;
    public static ConfigManager CONFIG;
    public static ModuleManager MODULES;

    public void init()
    {
        NETWORK = new NetworkManager();
        MODULES = new ModuleManager();
        CONFIG  = new ConfigManager(); // configs last.
    }
}
