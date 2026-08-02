package net.shoreline.client.impl.modules.combat.crystal;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.block.AsyncBlockState;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;
import net.shoreline.client.impl.mining.MiningData;
import net.shoreline.client.impl.modules.combat.AutoCrystalModule;
import net.shoreline.client.impl.modules.combat.util.MovementExtrapolation;
import net.shoreline.client.impl.modules.world.SpeedMineModule;
import net.shoreline.client.util.entity.PlayerUtil;
import net.shoreline.client.util.level.RaytraceUtil;

import java.util.ArrayList;
import java.util.List;

public class CrystalBaseScanner extends CrystalCevScanner<List<CrystalData<?>>>
{
    protected CrystalBaseScanner(AutoCrystalModule module)
    {
        super(module);
    }

    @Override
    protected void visit(BlockPos pos, AsyncBlockState state, List<CrystalData<?>> data)
    {
        AABB crystalBB = module.getCrystalBox(pos.above());
        if (hasEntityBlockingCrystal(crystalBB))
        {
            return;
        }

        double placeDist = getLocalPlayer().getEyePosition().distanceToSqr(pos.getCenter());
        if (placeDist > Mth.square(module.getPlaceTrace().getValue())
                && !RaytraceUtil.canSee(new Vec3(pos.above()), getLocalPlayer().getEntity()))
        {
            return;
        }

        if (placeDist > Mth.square(module.getPlaceRange().getValue()))
        {
            return;
        }

        Vec3 explosionCenter = pos.getBottomCenter().add(0.0, 1.0, 0.0);

        Vec3 localPos = getLocalPlayer().getPosition();
        AABB localBox = getLocalPlayer().getBoundingBox();
        float local = !PlayerUtil.isInSurvival(mc.player) ? 0.0f :
                getExplosionDamage(explosionCenter, localPos, localBox, module.getIgnoreTerrain().getValue());

        for (LivingEntityState entity : getPlayers())
        {
            if (Managers.SOCIAL.isFriend(entity.getName())
                    || entity.getArmor() <= 0 && !module.getTargetNakeds().getValue()
                    || !module.isValid(entity.getEntityType()))
            {
                continue;
            }

            int ticks = module.getExtrapolateTicks().getValue();
            Vec3 entityPos = ticks <= 0 ? entity.getPosition() : MovementExtrapolation.extrapolatePosition(this,
                    box -> getBlockCollisions(entity, box),
                    entity.getVelocity(),
                    entity.getBoundingBox(),
                    ticks);

            double blockDist = explosionCenter.distanceToSqr(entityPos);
            if (blockDist > 144.0f)
            {
                continue;
            }

            double dist = getLocalPlayer().squaredDistanceTo(entityPos);
            if (dist > Mth.square(module.getTargetRange().getValue()))
            {
                continue;
            }

            AABB boundingBox = entity.getDimensions().makeBoundingBox(entityPos);
            float damage = getExplosionDamage(explosionCenter,
                    entityPos,
                    boundingBox,
                    module.getIgnoreTerrain().getValue());

            data.add(factory.createData(pos, explosionCenter, entity, damage, local, () -> getImmediateTag(pos, explosionCenter)));
        }
    }

    @Override
    protected List<CrystalData<?>> createData()
    {
        return new ArrayList<>();
    }

    private boolean hasEntityBlockingCrystal(AABB box)
    {
        for (EntityState entity1 : getOtherEntities(null, box))
        {
            if (!module.canIgnoreEntity(entity1.getEntityType(), false))
            {
                return true;
            }
        }

        return false;
    }

    private String getImmediateTag(BlockPos pos, Vec3 crystalVec)
    {
        if (!SpeedMineModule.INSTANCE.isUsedByAutoMine())
        {
            return null;
        }

        Player target = Managers.TARGETING.getTarget();
        MiningData currentMine = SpeedMineModule.INSTANCE.getMainMiningBlock();
        if (target == null || currentMine == null)
        {
            return null;
        }

        if (module.getTargetItems().getValue() && isSurroundBreakPos(pos, crystalVec, target, currentMine))
        {
            return "AS";
        }

        if (module.getCevBreak().getValue() && isCevBreakerPos(pos, target, currentMine))
        {
            return "Cev";
        }

        return null;
    }
}
