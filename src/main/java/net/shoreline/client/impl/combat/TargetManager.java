package net.shoreline.client.impl.combat;

import lombok.Getter;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.shoreline.client.api.common.Feature;
import net.shoreline.client.impl.Managers;

public class TargetManager extends Feature
{
    /** All combat modules share the same target **/
    @Getter
    private Player target;

    public TargetManager()
    {
        super("Combat Target");
    }

    public void clearTarget()
    {
        target = null;
    }

    public Player setClosestTarget(float targetRange)
    {
        return target = getClosestTarget(targetRange);
    }

    public Player getClosestTarget(float targetRange)
    {
        Player best = null;
        double bestDist = Double.MAX_VALUE;
        for (Player entity : mc.level.players())
        {
            if (entity == mc.player || !entity.isAlive() || entity.isRemoved() || Managers.SOCIAL.isFriend(entity))
            {
                continue;
            }

            double dist = mc.player.distanceToSqr(entity);
            if (dist > Mth.square(targetRange))
            {
                continue;
            }

            if (dist < bestDist)
            {
                best = entity;
                bestDist = dist;
            }
        }

        return best;
    }

    public boolean hasTarget()
    {
        return target != null;
    }
}
