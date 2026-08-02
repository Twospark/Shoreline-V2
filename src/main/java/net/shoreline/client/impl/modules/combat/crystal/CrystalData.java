package net.shoreline.client.impl.modules.combat.crystal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;
import net.shoreline.client.impl.modules.combat.AutoCrystalModule;

import java.util.TreeSet;

@AllArgsConstructor
@Data
public class CrystalData<T>
{
    private T value;
    private Vec3 crystalVec;
    @Exclude
    private LivingEntityState target;

    @Exclude
    private double damageToTarget, damageToPlayer;

    @Getter
    public static class Immediate<T> extends CrystalData<T>
    {
        private final String tag;

        public Immediate(String tag,
                         T value,
                         Vec3 crystalVec,
                         LivingEntityState target,
                         float damageToTarget,
                         float damageToPlayer)
        {
            super(value, crystalVec, target, damageToTarget, damageToPlayer);
            this.tag = tag;
        }

        public Immediate(T value,
                         Vec3 crystalVec,
                         LivingEntityState target,
                         float damageToTarget,
                         float damageToPlayer)
        {
            this(null, value, crystalVec, target, damageToTarget, damageToPlayer);
        }
    }
}