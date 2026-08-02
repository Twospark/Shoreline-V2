package net.shoreline.client.impl.network;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

@UtilityClass
public class NetworkUtil
{
    public String getServerIp()
    {
        ServerData info = Minecraft.getInstance().getCurrentServer();
        if (info != null)
        {
            return info.ip;
        }

        return "Singleplayer";
    }
}
