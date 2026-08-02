package net.shoreline.client.impl.modules.combat.crystal;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;
import net.shoreline.client.impl.level.explosion.ExplosionUtil;
import net.shoreline.client.impl.modules.combat.AutoCrystalModule;
import net.shoreline.client.impl.modules.combat.util.AsyncDamageUtil;
import net.shoreline.client.util.item.ItemUtil;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class CrystalDataFactory
{
    private final AutoCrystalModule module;

    public <T> CrystalData<T> createData(T value,
                                         Vec3 crystalVec,
                                         LivingEntityState target,
                                         float damageToTarget,
                                         float damageToPlayer)
    {
        return createData(value, crystalVec, target, damageToTarget, damageToPlayer, null);
    }

    public <T> CrystalData<T> createData(T value,
                                         Vec3 crystalVec,
                                         LivingEntityState target,
                                         float damageToTarget,
                                         float damageToPlayer,
                                         Supplier<String> immediateTag)
    {
        if (module.getOverrideConfig().getValue()
                && (isLethalCrystal(target, damageToTarget)
                || isArmorBreaker(target, damageToTarget)))
        {
            return new CrystalData.Immediate<>(value,
                    crystalVec,
                    target,
                    damageToTarget,
                    damageToPlayer);
        }

        if (immediateTag != null)
        {
            String tagText = immediateTag.get();
            if (tagText != null)
            {
                return new CrystalData.Immediate<>(tagText,
                        value,
                        crystalVec,
                        target,
                        damageToTarget,
                        damageToPlayer);
            }
        }

        return new CrystalData<>(value,
                crystalVec,
                target,
                damageToTarget,
                damageToPlayer);
    }

    private boolean isLethalCrystal(LivingEntityState target, float damageToTarget)
    {
        return target.getHealth() - (AsyncDamageUtil.getAssumedDamage(damageToTarget, target) * module.getDamageMultiplier().getValue()) < 0.0f;
    }

    private boolean isArmorBreaker(LivingEntityState target, float damage)
    {
        for (ItemStack armorStack : target.getArmorItems())
        {
            if (armorStack.isEmpty())
            {
                continue;
            }

            int armorDamage = ExplosionUtil.getArmorDurabilityDamage(armorStack, damage);
            float durability = ItemUtil.getDurability(armorStack) - (armorDamage * module.getArmorMultiplier().getValue());
            if (durability <= 0)
            {
                return true;
            }
        }

        return false;
    }
}