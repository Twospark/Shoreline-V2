package net.shoreline.client.impl.event.input;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.player.ClientInput;
import net.shoreline.eventbus.Event;

@Getter
@Setter
public class PlayerInputEvent extends Event
{
    private ClientInput input;

    public PlayerInputEvent(ClientInput input)
    {
        this.input = input;
    }
}