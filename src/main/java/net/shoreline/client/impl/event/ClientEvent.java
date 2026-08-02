package net.shoreline.client.impl.event;

public class ClientEvent
{
    /** Posted when the client is fully initialized. */
    public static class Loaded {}

    /** Posted when Minecraft has fully loaded. */
    public static class McLoaded {}

    public static class ShutDown {}
}