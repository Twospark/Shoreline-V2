package net.shoreline.client.impl.level.entity.state;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Getter
public class EntityState
{
    private final String name;
    private final int id;
    private final EntityType<?> entityType;
    private final EntityDimensions dimensions;

    private final Vec3 position;
    private final Vec3 eyePosition;
    private final Vec3 velocity;
    private final AABB boundingBox;

    private final boolean alive;
    private final boolean descending;
    private final int age;

    public EntityState(Entity entity)
    {
        this.name = entity.getName().getString();
        this.id = entity.getId();
        this.entityType = entity.getType();
        this.dimensions = entity.getDimensions(entity.getPose());
        this.position = entity.position();
        this.eyePosition = entity.getEyePosition();
        this.velocity = entity.getDeltaMovement();
        this.boundingBox = entity.getBoundingBox();
        this.alive = entity.isAlive();
        this.descending = entity.isDescending();
        this.age = entity.tickCount;
    }

    public double getX()
    {
        return position.x;
    }

    public double getY()
    {
        return position.y;
    }

    public double getZ()
    {
        return position.z;
    }

    public BlockPos getBlockPos()
    {
        return BlockPos.containing(position);
    }

    public double squaredDistanceTo(Vec3 pos)
    {
        return this.position.distanceToSqr(pos);
    }

    /** YOU CAN ONLY CALL THE BELOW METHODS ON MC THREAD **/

    @SuppressWarnings("unchecked")
    public Entity getEntity()
    {
        return Minecraft.getInstance().level.getEntity(id);
    }

    public boolean isDead()
    {
        return getEntity() == null || !alive;
    }
}
