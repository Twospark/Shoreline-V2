package net.shoreline.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * @author linus
 * @since 2.0
 */
public class ShorelineMod implements ClientModInitializer
{
    public static final String MOD_NAME = "Shoreline";
    public static final String MOD_ID = "shoreline";
    public static final String MOD_VER = BuildConfig.VERSION;
    public static final String MOD_MC_VER = "26.1.2";

    /**
     * This code runs as soon as Minecraft is in a mod-load-ready state.
     * However, some things (like resources) may still be uninitialized.
     * Proceed with mild caution.
     */
    @Override
    public void onInitializeClient()
    {
        Shoreline.init();
    }

    public static boolean isBaritonePresent()
    {
        return FabricLoader.getInstance().getModContainer("baritone").isPresent();
    }

    public static String getSimpleVersion()
    {
        String[] split = MOD_VER.split("\\.");
        if (split.length == 1)
        {
            return MOD_VER;
        }

        for (int i = 1; i < split.length; i++)
        {
            if (!split[i].equals("0"))
            {
                return MOD_VER;
            }
        }

        return split[0];
    }
}
