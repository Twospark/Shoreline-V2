package net.shoreline.client.impl.block;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;

public interface AsyncCollisionView extends CollisionGetter
{
    Iterable<VoxelShape> getBlockCollisions(LivingEntityState living, AABB boundingBox);
}
