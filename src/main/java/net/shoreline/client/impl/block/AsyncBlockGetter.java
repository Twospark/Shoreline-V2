package net.shoreline.client.impl.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AsyncBlockGetter implements BlockGetter
{
    protected final ConcurrentMap<BlockPos, AsyncBlockState> blockStates = new ConcurrentHashMap<>();

    @Override
    public BlockState getBlockState(BlockPos pos)
    {
        return blockStates.get(pos).getBlockState();
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos)
    {
        return blockStates.get(pos).getBlockEntity();
    }

    @Override
    public FluidState getFluidState(BlockPos pos)
    {
        return blockStates.get(pos).getFluidState();
    }
}
