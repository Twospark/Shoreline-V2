package net.shoreline.client.util.entity;

import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.player.Player;

@UtilityClass
public class PlayerUtil
{
    public boolean isInSurvival(Player player)
    {
        return !player.isCreative() && !player.isSpectator();
    }
}