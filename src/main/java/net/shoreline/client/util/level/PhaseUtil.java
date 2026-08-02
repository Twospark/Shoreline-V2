package net.shoreline.client.util.level;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PhaseUtil
{
    public List<BlockPos> intersectingBlocks(AABB box)
    {
        Level level = Minecraft.getInstance().level;
        VoxelShape entityShape = Shapes.create(box);

        int minX = Mth.floor(box.minX);
        int minY = Mth.floor(box.minY);
        int minZ = Mth.floor(box.minZ);
        int maxX = Mth.floor(box.maxX);
        int maxY = Mth.floor(box.maxY);
        int maxZ = Mth.floor(box.maxZ);

        List<BlockPos> out = new ArrayList<>();
        for (int x = minX; x <= maxX; x++)
        {
            for (int y = minY; y <= maxY; y++)
            {
                for (int z = minZ; z <= maxZ; z++)
                {
                    BlockPos pos = new BlockPos(x, y, z);
                    VoxelShape collisionShape = level.getBlockState(pos).getCollisionShape(level, pos);
                    if (collisionShape.isEmpty())
                    {
                        continue;
                    }

                    if (Shapes.joinIsNotEmpty(collisionShape.move(x, y, z), entityShape, BooleanOp.AND))
                    {
                        out.add(pos.immutable());
                    }
                }
            }
        }

        return out;
    }

    public boolean isInsideBedrock(Entity entity)
    {
        return getFeetBlocks(entity).stream().anyMatch(BlockUtil::isUnbreakable);
    }

    public boolean isInsideBedrockWall(Entity entity)
    {
        return getWallBlocks(entity).stream().anyMatch(BlockUtil::isUnbreakable);
    }

    public boolean isInsideBlock(Entity entity)
    {
        return !getFeetBlocks(entity).isEmpty();
    }

    public boolean isInsideWall(Entity entity)
    {
        return !getWallBlocks(entity).isEmpty();
    }

    public List<BlockPos> getFeetBlocks(Entity entity)
    {
        if (entity.isVisuallyCrawling())
        {
            return new ArrayList<>();
        }

        AABB box = entity.getBoundingBox();
        AABB feetBox = new AABB(box.minX, box.minY, box.minZ, box.maxX, box.minY + 0.1, box.maxZ);
        return intersectingBlocks(feetBox);
    }

    public List<BlockPos> getWallBlocks(Entity entity)
    {
        if (entity.isVisuallyCrawling())
        {
            return new ArrayList<>();
        }

        AABB box = entity.getBoundingBox();
        AABB bodyBox = new AABB(box.minX, box.minY + 1.0, box.minZ, box.maxX, box.minY + 1.1, box.maxZ);
        return intersectingBlocks(bodyBox);
    }

    public boolean isInsideWeb(Entity entity)
    {
        return false;
    }
}