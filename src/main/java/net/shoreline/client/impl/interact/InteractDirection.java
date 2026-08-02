package net.shoreline.client.impl.interact;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.shoreline.client.impl.modules.client.InteractionsModule;

@UtilityClass
public class InteractDirection
{
    private final InteractionsModule interactConfig = InteractionsModule.INSTANCE;

    public Direction getInteractDirection(BlockPos blockPos)
    {
        return getInteractDirection(blockPos, interactConfig.getStrictDirection().getValue());
    }

    public Direction getInteractDirection(BlockPos blockPos, boolean strictDir)
    {
        Direction interactDirection = null;
        for (final Direction direction : Direction.values())
        {
            Direction opposite = direction.getOpposite();
            if (strictDir && isDirectionHidden(blockPos, opposite))
            {
                continue;
            }

            BlockState state = Minecraft.getInstance().level.getBlockState(blockPos.relative(direction));
            if (state.isAir() || !state.getFluidState().isEmpty())
            {
                continue;
            }

            if (state.is(Blocks.ANVIL) || state.is(Blocks.CHIPPED_ANVIL) || state.is(Blocks.DAMAGED_ANVIL))
            {
                continue;
            }

            interactDirection = opposite;
            break;
        }

        return interactDirection;
    }

    public boolean isDirectionHidden(BlockPos blockPos, Direction direction)
    {
        Player player = Minecraft.getInstance().player;
        double x = player.getX();
        double y = player.getEyeY();
        double z = player.getZ();

        AABB blockBox = new AABB(blockPos);
        if (blockBox.contains(x, y, z))
        {
            return false;
        }

        return switch (direction)
        {
            case NORTH -> z > blockBox.minZ; // Z- face
            case SOUTH -> z < blockBox.maxZ; // Z+ face
            case EAST  -> x < blockBox.maxX; // X+ face
            case WEST  -> x > blockBox.minX; // X- face
            case DOWN  -> y > blockBox.minY; // Y- face
            case UP -> false;
        };
    }
}