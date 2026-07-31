package net.shoreline.client.impl.interact;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.shoreline.client.impl.network.NetworkHandler;

@Getter
@Setter
public abstract class Interaction<T> extends NetworkHandler
{
    protected T interact;
    protected InteractionHand hand;
    protected boolean clientInteract;

    private final long interactionTime;

    protected InteractStatus status = InteractStatus.UNCONFIRMED;

    public Interaction(String name, T interact, InteractionHand hand, boolean clientInteract)
    {
        super(name);
        this.interact = interact;
        this.hand = hand;
        this.clientInteract = clientInteract;
        this.interactionTime = System.currentTimeMillis();
    }

    public Interaction(String name, InteractionHand hand, boolean clientInteract)
    {
        super(name);
        this.hand = hand;
        this.clientInteract = clientInteract;
        this.interactionTime = System.currentTimeMillis();
    }

    public abstract InteractionResult applyInteraction();
}