package net.shoreline.client.impl.interact;

import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.rotation.util.Rotation;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemInteraction extends Interaction<Item>
{
    public static final AtomicInteger GLOBAL_COUNT = new AtomicInteger();

    private final Rotation rotation;

    public ItemInteraction(Item interact, InteractionHand hand, Rotation rotation, boolean clientInteract)
    {
        super("ItemUseInteraction", interact, hand, clientInteract);
        this.rotation = rotation;
    }

    public ItemInteraction(Item interact, InteractionHand hand, boolean clientInteract)
    {
        super("ItemUseInteraction", interact, hand, clientInteract);
        this.rotation = Managers.ROTATION.hasClientRotation() ? Managers.ROTATION.getClientRotation() : new Rotation(mc.player);
    }

    @Override
    public InteractionResult applyInteraction()
    {
        if (!clientInteract || mc.isSameThread())
        {
            sendSequencedPacket(id -> new ServerboundUseItemPacket(hand, id, rotation.getYaw(), rotation.getPitch()));
            return InteractionResult.SUCCESS;
        }
        else
        {
            Rotation playerRotation = new Rotation(mc.player);
            rotation.applyToPlayer();
            InteractionResult result = mc.gameMode.useItem(mc.player, hand);
            playerRotation.applyToPlayer();
            return result;
        }
    }
}