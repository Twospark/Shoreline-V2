package net.shoreline.client.impl.modules.combat.crystal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;
import net.shoreline.client.impl.mining.MiningData;
import net.shoreline.client.impl.modules.combat.AutoCrystalModule;
import net.shoreline.client.impl.modules.combat.trap.TrapLayer;
import net.shoreline.client.impl.modules.combat.trap.TrapPositionCalc;
import net.shoreline.client.impl.modules.combat.trap.TrapSpec;
import net.shoreline.client.impl.modules.combat.util.AsyncDamageUtil;

import java.util.EnumSet;
import java.util.Set;

public abstract class CrystalTrapScanner<T> extends CrystalEntityScanner<T>
{
    private static final EntityDimensions ITEM_DIMENSIONS
            = EntityDimensions.fixed(0.25f, 0.25f);

    private static final TrapSpec FEET_TRAP_SPEC = TrapSpec.builder()
            .layers(EnumSet.of(TrapLayer.FEET))
            .extendBody(false)
            .extendFeet(false)
            .build();

    private final TrapPositionCalc trapPositionCalc = new TrapPositionCalc();

    public CrystalTrapScanner(AutoCrystalModule module)
    {
        super(module);
    }

    protected boolean isSurroundBreakPos(BlockPos blockPos,
                                         Vec3 crystalVec,
                                         Player target,
                                         MiningData currentMine)
    {
        BlockPos minePos = currentMine.getBlockPos();
        LivingEntityState state = (LivingEntityState) getEntity(target.getId());

        trapPositionCalc.calcTrap(state.getBoundingBox(), FEET_TRAP_SPEC);
        if (!trapPositionCalc.getTrapPositions().contains(minePos))
        {
            return false;
        }

        if (currentMine.isDoneMining())
        {
            float baseDamage = getAssumedDamage(minePos.getBottomCenter(), minePos, state);
            if (baseDamage < module.getMinDamage().getValue())
            {
                return false;
            }

            for (EntityState entityState : getOtherEntities(null, new AABB(minePos)))
            {
                if (entityState.getEntityType() != EntityType.ITEM)
                {
                    continue;
                }

                if (getExplosionDamage(crystalVec,
                        entityState.getPosition(),
                        entityState.getBoundingBox(),
                        false,
                        Set.of(minePos)) >= 5.0f)
                {
                    return true;
                }
            }
        }

        else if (currentMine.isAlmostDone(module.getPrePlace().getValue()))
        {
            Vec3 simPos = minePos.getBottomCenter();
            return getExplosionDamage(blockPos.above().getBottomCenter(),
                    simPos,
                    ITEM_DIMENSIONS.makeBoundingBox(simPos),
                    false,
                    Set.of(minePos)) >= 5.0f;
        }

        return false;
    }

    protected float getAssumedDamage(Vec3 crystalVec, BlockPos ignore, LivingEntityState state)
    {
        return AsyncDamageUtil.getAssumedDamage(getExplosionDamage(crystalVec,
                state.getPosition(),
                state.getBoundingBox(),
                module.getIgnoreTerrain().getValue(),
                Set.of(ignore)), state);
    }
}
