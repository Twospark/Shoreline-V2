package net.shoreline.client.impl.block;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;

@RequiredArgsConstructor
public class AsyncCollisionContext implements CollisionContext
{
    private final LivingEntityState entityState;

    @Override
    public boolean isDescending()
    {
        return entityState.isDescending();
    }

    @Override
    public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue)
    {
        return entityState.getY() > pos.getY() + shape.max(Direction.Axis.Y) - 1.0E-5f;
    }

    @Override
    public boolean isHoldingItem(Item item)
    {
        return entityState.getHeldItem().equals(item);
    }

    @Override
    public boolean alwaysCollideWithFluid()
    {
        return false;
    }

    @Override
    public boolean canStandOnFluid(FluidState fluidStateAbove, FluidState fluid)
    {
        return false;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, CollisionGetter collisionGetter, BlockPos pos)
    {
        return state.getCollisionShape(collisionGetter, pos, this);
    }
}
