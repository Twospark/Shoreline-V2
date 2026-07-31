package net.shoreline.client.impl.event.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RotationUpdateEvent
{
    private final float yaw, pitch;

    public static class Pre extends RotationUpdateEvent
    {
        public Pre()
        {
            super(0.0f, 0.0f);
        }
    }

    public static class PrePacket extends RotationUpdateEvent
    {
        public PrePacket()
        {
            super(0.0f, 0.0f);
        }
    }
}