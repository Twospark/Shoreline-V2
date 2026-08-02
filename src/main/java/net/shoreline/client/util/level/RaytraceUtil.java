package net.shoreline.client.util.level;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@UtilityClass
public class RaytraceUtil
{
    public boolean canSee(Vec3 toSee, Entity entity)
    {
        ClipContext context = new ClipContext(entity.getEyePosition(), toSee, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
        return Minecraft.getInstance().level.clip(context).getType() == HitResult.Type.MISS;
    }
}
