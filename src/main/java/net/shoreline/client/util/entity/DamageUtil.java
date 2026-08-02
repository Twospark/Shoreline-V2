package net.shoreline.client.util.entity;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.shoreline.client.impl.level.explosion.ExplosionUtil;

@UtilityClass
public class DamageUtil
{
    public float getHealth(LivingEntity entity)
    {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public boolean willDamageKillEntity(double damage, LivingEntity entity)
    {
        return getHealth(entity) - damage < 0.5f;
    }

    public double getCrystalDamage(LivingEntity entity)
    {
        double crystalDmg = 0.0;
        for (Entity e : Minecraft.getInstance().level.entitiesForRendering())
        {
            if (e instanceof EndCrystal crystal && entity.distanceToSqr(e) <= 144.0)
            {
                double damage = ExplosionUtil.crystalDamageToEntity(Minecraft.getInstance().level, entity, crystal.position());
                if (damage > crystalDmg)
                {
                    crystalDmg = damage;
                }
            }
        }

        return crystalDmg;
    }

    public int getFallDamage(LivingEntity entity, double fallDistance, float damageMultiplier)
    {
        final MobEffectInstance statusEffectInstance = entity.getEffect(MobEffects.JUMP_BOOST);
        final float f = statusEffectInstance == null ? 0.0f : (float) (statusEffectInstance.getAmplifier() + 1);
        return Mth.ceil((fallDistance - 3.0f - f) * damageMultiplier);
    }

    public float getArmorToughness(LivingEntity living)
    {
        return (float) Math.floor(living.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
    }

    public float getArmor(LivingEntity living)
    {
        return (float) Math.floor(living.getAttributeValue(Attributes.ARMOR));
    }
}
