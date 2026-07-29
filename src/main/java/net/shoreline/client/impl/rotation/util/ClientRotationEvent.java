package net.shoreline.client.impl.rotation.util;

import lombok.Getter;
import lombok.Setter;
import net.shoreline.eventbus.Event;

public class ClientRotationEvent extends Event
{
    @Getter
    @Setter
    private Rotation rotation;

    public ClientRotationEvent(Rotation rotation)
    {
        this.rotation = rotation;
    }

    public void setYaw(float yaw)
    {
        setCanceled(true);
        rotation.setYaw(yaw);
    }

    public void setPitch(float pitch)
    {
        setCanceled(true);
        rotation.setPitch(pitch);
    }
}
