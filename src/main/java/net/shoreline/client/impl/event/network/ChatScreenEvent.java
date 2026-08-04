package net.shoreline.client.impl.event.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.shoreline.eventbus.Event;

public class ChatScreenEvent
{
    @RequiredArgsConstructor
    @Getter
    public static class SendMessage extends Event
    {
        private final String message;
    }
}
