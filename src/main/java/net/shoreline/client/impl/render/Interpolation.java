package net.shoreline.client.impl.render;

import lombok.experimental.UtilityClass;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@UtilityClass
public class Interpolation
{
    /**
     * Gets the interpolated {@link Vec3} position of an entity (i.e. position
     * based on render ticks)
     *
     * @param entity       The entity to get the position for
     * @param partialTicks The render time
     * @return The interpolated vector of an entity
     */
    public Vec3 getRenderPosition(Entity entity, float partialTicks)
    {
        return new Vec3(Mth.lerp(partialTicks, entity.xo, entity.getX()),
                Mth.lerp(partialTicks, entity.yo, entity.getY()),
                Mth.lerp(partialTicks, entity.zo, entity.getZ()));
    }

    public Vec3 getRenderPosition(Vec3 pos, Vec3 lastPos, float partialTicks)
    {
        return pos.subtract(Mth.lerp(partialTicks, lastPos.x, pos.x),
                Mth.lerp(partialTicks, lastPos.y, pos.y),
                Mth.lerp(partialTicks, lastPos.z, pos.z));
    }

    public AABB getEntityRenderBox(Entity entity, float partialTicks)
    {
        AABB box = entity.getBoundingBox();
        AABB lastBox = box.move(entity.xo - entity.getX(),
                entity.xo - entity.getY(),
                entity.zo - entity.getZ());

        return getRenderBox(box, lastBox, partialTicks);
    }

    public AABB getRenderBox(AABB box, AABB lastBox, float partialTicks)
    {
        return new AABB(Mth.lerp(partialTicks, lastBox.minX, box.minX),
                Mth.lerp(partialTicks, lastBox.minY, box.minY),
                Mth.lerp(partialTicks, lastBox.minZ, box.minZ),
                Mth.lerp(partialTicks, lastBox.maxX, box.maxX),
                Mth.lerp(partialTicks, lastBox.maxY, box.maxY),
                Mth.lerp(partialTicks, lastBox.maxZ, box.maxZ));
    }
}