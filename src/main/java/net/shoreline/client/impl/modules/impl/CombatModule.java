package net.shoreline.client.impl.modules.impl;

import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.shoreline.client.api.module.Category;

public class CombatModule extends EntityTypeModule
{
    public CombatModule(String name, String description, Category category)
    {
        super(name, description, category);
    }

    public CombatModule(String name, String[] nameAliases, String description, Category category)
    {
        super(name, nameAliases, description, category);
    }

    public void sendAttackPackets(Entity entity, boolean swing)
    {
        sendAttackPacketsInternal(entity.getId(), swing, InteractionHand.MAIN_HAND);
    }

    public void sendAttackPacketsInternal(int entityId, boolean swing, InteractionHand hand)
    {
        ServerboundAttackPacket attackPacket = new ServerboundAttackPacket(entityId);
        sendPacket(attackPacket);

        if (swing)
        {
            mc.player.swing(hand);
        }
        else
        {
            sendPacket(new ServerboundSwingPacket(hand));
        }
    }
}
