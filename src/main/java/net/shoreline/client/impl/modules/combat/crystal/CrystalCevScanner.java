package net.shoreline.client.impl.modules.combat.crystal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.shoreline.client.impl.mining.MiningData;
import net.shoreline.client.impl.modules.combat.AutoCrystalModule;

public abstract class CrystalCevScanner<T> extends CrystalTrapScanner<T>
{
    private boolean startedCevSequence;

    protected CrystalCevScanner(AutoCrystalModule module)
    {
        super(module);
    }

    protected boolean isCevBreakerPos(BlockPos blockPos,
                                      Player target,
                                      MiningData currentMine)
    {
//        BlockPos targetHeadPos = EntityUtil.getRoundedBlockPos(target).up(target.isCrawling() ? 1 : 2);
//        if (!blockPos.down().equals(targetHeadPos) || !currentMine.getBlockPos().equals(targetHeadPos))
//        {
//            return false;
//        }

//        if (startedCevSequence)
//        {
//            if (!currentMine.isDoneMining())
//            {
//                return false;
//            }
//
//            startedCevSequence = false;
//            return true;
//        }
//
//        else if (currentMine.isAlmostDone(5))
//        {
//            return startedCevSequence = true;
//        }

        return false;
    }
}