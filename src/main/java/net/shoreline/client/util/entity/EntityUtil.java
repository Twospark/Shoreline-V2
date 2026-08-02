package net.shoreline.client.util.entity;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;

@UtilityClass
public class EntityUtil
{
    public boolean isHostile(final Entity entity)
    {
        return entity instanceof Monster && !isNeutral(entity);
    }

    public boolean isPassive(Entity entity)
    {
        return entity instanceof Animal || entity instanceof AmbientCreature || entity instanceof Squid;
    }

    public boolean isHostile(EntityType<?> type)
    {
        return type.getCategory() == MobCategory.MONSTER;
    }

    public boolean isPassive(EntityType<?> type)
    {
        MobCategory group = type.getCategory();
        return group == MobCategory.CREATURE || group == MobCategory.AMBIENT
                || type == EntityType.SQUID || type == EntityType.GLOW_SQUID;
    }

    private boolean isNeutral(Entity entity)
    {
        return entity instanceof EnderMan enderman && !enderman.isAngry()
                || entity instanceof ZombifiedPiglin piglin && !piglin.isAngry()
                || entity instanceof Wolf wolf && !wolf.isAngry()
                || entity instanceof IronGolem ironGolem && !ironGolem.isAngry()
                || entity instanceof Bee bee && !bee.isAngry();
    }

    public BlockPos getRoundedBlockPos(Entity entity)
    {
        return BlockPos.containing(entity.getBlockX(), Math.round(entity.getY()), entity.getBlockZ());
    }
}
