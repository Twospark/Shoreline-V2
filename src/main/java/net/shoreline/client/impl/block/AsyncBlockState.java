package net.shoreline.client.impl.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

@RequiredArgsConstructor
@Getter
public class AsyncBlockState
{
    private final BlockState blockState;
    private final FluidState fluidState;
    private final BlockEntity blockEntity;

    public static AsyncBlockState getDefaultState()
    {
        BlockState state = Blocks.AIR.defaultBlockState();
        return new AsyncBlockState(state, state.getFluidState(), null);
    }
}