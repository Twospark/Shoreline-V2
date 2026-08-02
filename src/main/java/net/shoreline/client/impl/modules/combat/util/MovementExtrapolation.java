package net.shoreline.client.impl.modules.combat.util;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

@UtilityClass
public class MovementExtrapolation
{
    public Vec3 extrapolatePosition(CollisionGetter collisionView,
                                     Function<AABB, Iterable<VoxelShape>> blockCollisionFunction,
                                     Vec3 velocity,
                                     AABB box,
                                     int ticks)
    {
        return extrapolatePosition(collisionView, blockCollisionFunction, velocity, box, ticks, true);
    }

    public Vec3 extrapolatePosition(CollisionGetter collisionView,
                                    Function<AABB, Iterable<VoxelShape>> blockCollisionFunction,
                                    Vec3 velocity,
                                    AABB box,
                                    int ticks,
                                    boolean simulateY)
    {
        if (!simulateY)
        {
            velocity = velocity.multiply(1.0, 0.0, 1.0);
        }

        for (int i = 0; i < ticks; i++)
        {
            velocity = velocity.add(0.0, -0.08, 0.0).multiply(0.98, 0.98, 0.98);

            double dx = velocity.x;
            double dy = velocity.y;
            double dz = velocity.z;

            Iterable<VoxelShape> collisionsX = blockCollisionFunction.apply(getCollisionBox(box, Direction.Axis.X, dx));
            double collideX = dx == 0.0 ? 0.0 : Shapes.collide(Direction.Axis.X, box, collisionsX, dx);
            box = box.move(collideX, 0, 0);
            if (Math.abs(dx - collideX) > 1.0e-7)
            {
                velocity = new Vec3(0.0, velocity.y, velocity.z);
            }

            Iterable<VoxelShape> collisionsY = blockCollisionFunction.apply(getCollisionBox(box, Direction.Axis.Y, dy));
            double collideY = dy == 0.0 ? 0.0 : Shapes.collide(Direction.Axis.Y, box, collisionsY, dy);
            box = box.move(0, collideY, 0);
            boolean onGround = dy < 0.0 && Math.abs(dy - collideY) > 1.0E-7;
            if (Math.abs(dy - collideY) > 1.0e-7)
            {
                velocity = new Vec3(velocity.x, 0.0, velocity.z);
            }

            Iterable<VoxelShape> collisionsZ = blockCollisionFunction.apply(getCollisionBox(box, Direction.Axis.Z, dz));
            double collideZ = dz == 0.0 ? 0.0 : Shapes.collide(Direction.Axis.Z, box, collisionsZ, dz);
            box = box.move(0, 0, collideZ);
            if (Math.abs(dz - collideZ) > 1.0E-7)
            {
                velocity = new Vec3(velocity.x, velocity.y, 0.0);
            }

            if (onGround)
            {
                double friction = 0.91;
                try
                {
                    BlockPos below = BlockPos.containing(
                            (box.minX + box.maxX) * 0.5,
                            box.minY - 0.500001,
                            (box.minZ + box.maxZ) * 0.5
                    );

                    friction *= collisionView.getBlockState(below).getBlock().getSpeedFactor();
                }
                catch (Throwable ignored) {}

                velocity = new Vec3(velocity.x * friction, velocity.y, velocity.z * friction);
            }
        }

        double cx = (box.minX + box.maxX) * 0.5;
        double cz = (box.minZ + box.maxZ) * 0.5;
        return new Vec3(cx, box.minY, cz);
    }

    private AABB getCollisionBox(AABB box, Direction.Axis axis, double target)
    {
        return box.expandTowards(
                axis == Direction.Axis.X ? target : 0.0,
                axis == Direction.Axis.Y ? target : 0.0,
                axis == Direction.Axis.Z ? target : 0.0
        ).inflate(1.0e-7);
    }
}
