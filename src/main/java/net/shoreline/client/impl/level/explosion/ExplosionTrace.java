package net.shoreline.client.impl.level.explosion;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;

@UtilityClass
public class ExplosionTrace
{
    public float getDamageToPos(BlockGetter blockView,
                                Vec3 source,
                                Vec3 pos,
                                AABB box,
                                float power,
                                boolean ignoreTerrain)
    {
        return getDamageToPos(blockView, source, pos, box, power, ignoreTerrain, Collections.emptySet());
    }

    public float getDamageToPos(BlockGetter blockView,
                                Vec3 source,
                                Vec3 pos,
                                AABB box,
                                float power,
                                boolean ignoreTerrain,
                                Set<BlockPos> ignoreBlocks)
    {
        double d = Math.sqrt(pos.distanceToSqr(source));
        RaycastFactory raycastFactory = getRaycastFactory(blockView, ignoreTerrain, ignoreBlocks);
        double ab = getExposure(source, box, raycastFactory);
        double w = d / power;
        double ac = (1.0 - w) * ab;
        return (float) ((int) ((ac * ac + ac) / 2.0 * 7.0 * 12.0 + 1.0));
    }

    private float getExposure(Vec3 source, AABB box, RaycastFactory raycastFactory)
    {
        double xDiff = box.maxX - box.minX;
        double yDiff = box.maxY - box.minY;
        double zDiff = box.maxZ - box.minZ;

        double xStep = 1 / (xDiff * 2 + 1);
        double yStep = 1 / (yDiff * 2 + 1);
        double zStep = 1 / (zDiff * 2 + 1);

        if (xStep > 0 && yStep > 0 && zStep > 0)
        {
            int misses = 0;
            int hits = 0;

            double xOffset = (1 - Math.floor(1 / xStep) * xStep) * 0.5;
            double zOffset = (1 - Math.floor(1 / zStep) * zStep) * 0.5;

            xStep = xStep * xDiff;
            yStep = yStep * yDiff;
            zStep = zStep * zDiff;

            double startX = box.minX + xOffset;
            double startY = box.minY;
            double startZ = box.minZ + zOffset;
            double endX = box.maxX + xOffset;
            double endY = box.maxY;
            double endZ = box.maxZ + zOffset;

            for (double x = startX; x <= endX; x += xStep)
            {
                for (double y = startY; y <= endY; y += yStep)
                {
                    for (double z = startZ; z <= endZ; z += zStep)
                    {
                        Vec3 position = new Vec3(x, y, z);
                        if (raycast(new ExposureClipContext(position, source), raycastFactory) == null)
                        {
                            misses++;
                        }

                        hits++;
                    }
                }
            }

            return (float) misses / hits;
        }

        return 0f;
    }

    private RaycastFactory getRaycastFactory(BlockGetter blockView,
                                             boolean ignoreTerrain,
                                             Set<BlockPos> ignoreBlocks)
    {
        return (context, blockPos) ->
        {
            if (ignoreBlocks.contains(blockPos))
            {
                return null;
            }

            BlockState blockState = blockView.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (ignoreTerrain && block.getExplosionResistance() < 600)
            {
                return null;
            }

            VoxelShape voxelShape = blockState.getCollisionShape(blockView, blockPos);
            return voxelShape.clip(context.start, context.end, blockPos);
        };
    }

    private BlockHitResult raycast(ExposureClipContext context, RaycastFactory raycastFactory)
    {
        return BlockGetter.traverseBlocks(context.start, context.end, context, raycastFactory, ctx -> null);
    }

    @FunctionalInterface
    public interface RaycastFactory extends BiFunction<ExposureClipContext, BlockPos, BlockHitResult> {}

    public record ExposureClipContext(Vec3 start, Vec3 end) { }
}