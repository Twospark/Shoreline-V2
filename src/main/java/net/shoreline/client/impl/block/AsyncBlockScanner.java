package net.shoreline.client.impl.block;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.shoreline.client.api.interfaces.Globals;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AsyncBlockScanner<T> extends AsyncCollisionScanner implements Globals
{
    private final BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();

    private int height, bottomY;

    public void createCube(ClientLevel level, BlockPos center)
    {
        blockStates.clear();
        int radius = getRadius();

        height = center.getY() + radius;
        bottomY = center.getY() - radius;
        for (int dx = -radius; dx <= radius; ++dx)
        {
            for (int dy = -radius; dy <= radius; ++dy)
            {
                for (int dz = -radius; dz <= radius; ++dz)
                {
                    mPos.set(center.getX() + dx,
                            center.getY() + dy,
                            center.getZ() + dz);
                    BlockPos key = mPos.immutable();

                    AsyncBlockState blockState = new AsyncBlockState(
                            level.getBlockState(key),
                            level.getFluidState(key),
                            level.getBlockEntity(key)
                    );

                    blockStates.put(key, blockState);
                }
            }
        }
    }

    public void createSphere(ClientLevel level, BlockPos center)
    {
        blockStates.clear();

        final int r = getRadius();
        final int r2 = r * r;

        height = center.getY() + r;
        bottomY = center.getY() - r;
        for (int dx = -r; dx <= r; ++dx)
        {
            final int dx2 = dx * dx;
            for (int dy = -r; dy <= r; ++dy)
            {
                final int dxy2 = dx2 + dy * dy;
                for (int dz = -r; dz <= r; ++dz)
                {
                    if (dxy2 + dz * dz <= r2)
                    {
                        mPos.set(center.getX() + dx,
                                center.getY() + dy,
                                center.getZ() + dz);
                        BlockPos key = mPos.immutable();

                        AsyncBlockState blockState = new AsyncBlockState(
                                level.getBlockState(key),
                                level.getFluidState(key),
                                level.getBlockEntity(key)
                        );

                        blockStates.put(key, blockState);
                    }
                }
            }
        }
    }

    public T scanBlocks()
    {
        T data = createData();
        for (Map.Entry<BlockPos, AsyncBlockState> entry : blockStates.entrySet())
        {
            visit(entry.getKey(), entry.getValue(), data);
        }

        return data;
    }

    protected abstract void visit(BlockPos pos, AsyncBlockState state, T data);

    protected abstract int getRadius();

    protected abstract T createData();

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getMinY()
    {
        return bottomY;
    }
}
