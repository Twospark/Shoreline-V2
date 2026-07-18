package net.shoreline.client;

import net.shoreline.client.impl.Managers;
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
        Managers.init();
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

    public static void error(String s, int error)
    {
        Loader.error("{} {}", s, error);
    }

    public static void error(String s, Exception e)
    {
        Loader.error("{}", s, e);
    }

    public static void error(String s)
    {
        Loader.error("{}", s);
    }
}
