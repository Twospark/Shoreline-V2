package net.shoreline.client.impl.event.render;

import net.shoreline.eventbus.Event;

public class OverlayEvent extends Event
{
    public static class Fire extends OverlayEvent {}

    public static class Water extends OverlayEvent {}

    public static class Blocks extends OverlayEvent {}

    public static class Portal extends OverlayEvent {}

    public static class Frostbite extends OverlayEvent {}

    public static class Spyglass extends OverlayEvent {}

    public static class BossBar extends OverlayEvent {}
}
