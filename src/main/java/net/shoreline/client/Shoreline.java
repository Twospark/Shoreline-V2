package net.shoreline.client;

import net.shoreline.loader.Loader;

/**
 * Client main class. Handles main client mod initializing of static handler
 * instances and client managers.
 *
 * @author linus
 * @see ShorelineMod
 * @since 2.0
 */
public class Shoreline
{
    /**
     * Called during {@link ShorelineMod#onInitializeClient()}
     */
    public static void init()
    {
        info("Starting Shoreline...");
    }

    public static void postInit()
    {
    }

    public static void info(String message)
    {
        Loader.info(message);
    }

    public static void info(String message, Object... params)
    {
        Loader.info(message, params);
    }
}
