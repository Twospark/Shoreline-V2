package net.shoreline.client.impl.level.explosion;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.shoreline.client.impl.level.AsyncLevelScanner;

import java.util.Set;

@RequiredArgsConstructor
public abstract class ExplosionScanner<T> extends AsyncLevelScanner<T>
{
    private final float explosionPower;

    public float getExplosionDamage(Vec3 pos,
                                    Vec3 entityPos,
                                    AABB boundingBox,
                                    boolean ignoreTerrain)
    {
        return ExplosionTrace.getDamageToPos(this,
                pos,
                entityPos,
                boundingBox,
                explosionPower,
                ignoreTerrain);
    }

    public float getExplosionDamage(Vec3 pos,
                                    Vec3 entityPos,
                                    AABB boundingBox,
                                    boolean ignoreTerrain,
                                    Set<BlockPos> ignoredBlocks)
    {
        return ExplosionTrace.getDamageToPos(this,
                pos,
                entityPos,
                boundingBox,
                explosionPower,
                ignoreTerrain,
                ignoredBlocks);
    }
}