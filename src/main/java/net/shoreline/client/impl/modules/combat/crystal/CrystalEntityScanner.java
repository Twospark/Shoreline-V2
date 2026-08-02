package net.shoreline.client.impl.modules.combat.crystal;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.Managers;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;
import net.shoreline.client.impl.level.explosion.ExplosionScanner;
import net.shoreline.client.impl.modules.combat.AutoCrystalModule;
import net.shoreline.client.impl.modules.combat.util.MovementExtrapolation;
import net.shoreline.client.util.entity.PlayerUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class CrystalEntityScanner<T> extends ExplosionScanner<T>
{
    protected final AutoCrystalModule module;
    protected final CrystalDataFactory factory;

    public CrystalEntityScanner(AutoCrystalModule module)
    {
        super(6.0f);
        this.module = module;
        this.factory = new CrystalDataFactory(module);
    }

    @Override
    protected int getRadius()
    {
        return (int) Math.ceil(module.getTargetRange().getValue() + 1.0f);
    }

    public List<CrystalData<?>> scanCrystals()
    {
        List<CrystalData<?>> result = new ArrayList<>();
        for (EntityState state : getEntities())
        {
            if (!state.isAlive() || state.getEntityType() != EntityType.END_CRYSTAL)
            {
                continue;
            }

            visitCrystal(state, result);
        }

        return result;
    }

    public void visitCrystal(EntityState state, List<CrystalData<?>> result)
    {
        if (state.getAge() < module.getTicksExisted().getValue())
        {
            return;
        }

        double breakDist = getLocalPlayer().getEyePosition().distanceToSqr(state.getPosition());
        if (breakDist > Mth.square(module.getBreakRange().getValue()))
        {
            return;
        }

        Vec3 localPos = getLocalPlayer().getPosition();
        AABB localBox = getLocalPlayer().getBoundingBox();
        float local = !PlayerUtil.isInSurvival(mc.player) ? 0.0f :
                getExplosionDamage(state.getPosition(), localPos, localBox, module.getIgnoreTerrain().getValue());

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

            double dist = getLocalPlayer().squaredDistanceTo(entityPos);
            if (dist > Mth.square(module.getTargetRange().getValue()))
            {
                continue;
            }

            AABB boundingBox = entity.getDimensions().makeBoundingBox(entityPos);
            float damage = getExplosionDamage(state.getPosition(),
                    entityPos,
                    boundingBox,
                    module.getIgnoreTerrain().getValue());

            result.add(factory.createData(state, state.getPosition(), entity, damage, local));
        }
    }
}
