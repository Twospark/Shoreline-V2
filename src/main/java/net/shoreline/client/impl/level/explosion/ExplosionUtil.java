package net.shoreline.client.impl.level.explosion;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.modules.client.InventoryModule;
import net.shoreline.client.util.entity.DamageUtil;
import net.shoreline.client.util.item.EnchantUtil;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class ExplosionUtil
{
    public double crystalDamageToEntity(BlockGetter blockView,
                                        LivingEntity entity,
                                        Vec3 explosion)
    {
        return crystalDamageToEntity(blockView, entity, explosion, false, Collections.emptySet());
    }

    public double crystalDamageToEntity(BlockGetter blockView,
                                        LivingEntity entity,
                                        Vec3 explosion,
                                        boolean ignoreTerrain,
                                        Set<BlockPos> ignoreBlocks)
    {
        return damageToEntity(blockView, entity, explosion, 12.0f, ignoreTerrain, ignoreBlocks);
    }

    public double damageToEntity(BlockGetter blockView,
                                 LivingEntity entity,
                                 Vec3 explosion,
                                 float power,
                                 boolean ignoreTerrain,
                                 Set<BlockPos> ignoreBlocks)
    {
        float dmg = ExplosionTrace.getDamageToPos(blockView,
                explosion,
                entity.position(),
                entity.getBoundingBox(),
                power,
                ignoreTerrain,
                ignoreBlocks);

        return getAppliedDamageToEntity(entity, dmg);
    }

    public float getAppliedDamageToEntity(Entity entity, float damage)
    {
        return Math.max(0.0f, getReduction(entity, Minecraft.getInstance().level.damageSources().explosion(null), damage));
    }

    private float getReduction(Entity entity, DamageSource damageSource, float damage)
    {
        if (damageSource.scalesWithDifficulty())
        {
            switch (Minecraft.getInstance().level.getDifficulty())
            {
                // case PEACEFUL -> return 0;
                case EASY -> damage = Math.min(damage / 2 + 1, damage);
                case HARD -> damage *= 1.5f;
            }
        }

        if (entity instanceof LivingEntity livingEntity)
        {
            damage = CombatRules.getDamageAfterAbsorb(livingEntity, damage, damageSource, getArmor(livingEntity), (float) getAttributeValue(livingEntity, Attributes.ARMOR_TOUGHNESS));
            damage = getResistanceReduction(livingEntity, damage);
            damage = getProtectionReduction(livingEntity, damage);
        }

        return damage;
    }

    private float getArmor(LivingEntity entity)
    {
        return (float) Math.floor(getAttributeValue(entity, Attributes.ARMOR));
    }

    // TODO: Figure out why this is null
    private double getAttributeValue(LivingEntity entity, Holder<Attribute> attribute)
    {
        try
        {
            return entity.getAttributeValue(attribute);
        }
        catch (NullPointerException ignored)
        {
            return 0.0;
        }
    }

    private float getProtectionReduction(Entity player, float damage)
    {
        if (player instanceof LivingEntity livingEntity)
        {
            float protLevel = getProtectionAmount(livingEntity);
            return CombatRules.getDamageAfterMagicAbsorb(damage, protLevel);
        }

        return 0.0f;
    }

    private float getProtectionAmount(LivingEntity living)
    {
        MutableInt mutableInt = new MutableInt();
        for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR)
        {
            ItemStack stack = living.getItemBySlot(slot);
            if (InventoryModule.INSTANCE.getAssumeEnchanted().getValue() && EnchantUtil.isEnchantsObfuscated(stack))
            {
                DataComponentMap item = stack.getItem().components();
                if (item.has(DataComponents.EQUIPPABLE))
                {
                    mutableInt.add(item.get(DataComponents.EQUIPPABLE).slot().getIndex() == 2 ? 8 : 4);
                }
            }
            else
            {
                ItemEnchantments enchantments = stack.getEnchantments();
                for (Holder<Enchantment> enchantment : enchantments.keySet())
                {
                    if (enchantment.getRegisteredName().contains("protection"))
                    {
                        mutableInt.add(enchantments.getLevel(enchantment));
                    }

                    if (enchantment.getRegisteredName().contains("blast_protection"))
                    {
                        mutableInt.add(enchantments.getLevel(enchantment) * 2);
                    }
                }
            }
        }

        return mutableInt.intValue();
    }

    private float getResistanceReduction(LivingEntity player, float damage)
    {
        MobEffectInstance resistance = player.getEffect(MobEffects.RESISTANCE);
        if (resistance != null)
        {
            int lvl = resistance.getAmplifier() + 1;
            damage *= (1.0f - (lvl * 0.2f));
        }

        return Math.max(damage, 0.0f);
    }

    public int getArmorDurabilityDamage(ItemStack stack, float damage)
    {
        int dmg = (int) (damage / 4.0f);
        if (dmg < 1)
        {
            dmg = 1;
        }

        int level = EnchantUtil.getLevel(Enchantments.UNBREAKING, stack);
        double p = 0.6 + 0.4 / (level + 1.0);
        int result = 0;
        for (int i = 0; i < dmg; i++)
        {
            if (ThreadLocalRandom.current().nextDouble() < p)
            {
                result++;
            }
        }

        return result;
    }
}
