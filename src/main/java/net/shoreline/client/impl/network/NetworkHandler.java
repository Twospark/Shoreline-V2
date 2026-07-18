package net.shoreline.client.impl.network;

import net.shoreline.client.api.common.Feature;
import net.shoreline.client.impl.Managers;

public class NetworkHandler extends Feature
{
    public NetworkHandler(String name)
    {
        super(name);
        Managers.NETWORK.registerHandler(this);
    }

    public NetworkHandler(String name, String[] nameAliases)
    {
        super(name, nameAliases);
        Managers.NETWORK.registerHandler(this);
    }
}
