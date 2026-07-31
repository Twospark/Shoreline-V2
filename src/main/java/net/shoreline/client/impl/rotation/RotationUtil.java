package net.shoreline.client.impl.rotation;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

@UtilityClass
public class RotationUtil
{
    public float[] getRotationsTo(Vec3 src, Vec3 dest)
    {
        float yaw = (float) (Math.toDegrees(Math.atan2(dest.subtract(src).z,
                dest.subtract(src).x)) - 90.0f);
        float pitch = (float) Math.toDegrees(-Math.atan2(dest.subtract(src).y,
                Math.hypot(dest.subtract(src).x, dest.subtract(src).z)));

        float playerYaw = Minecraft.getInstance().player.getYRot();
        float playerPitch = Minecraft.getInstance().player.getXRot();

        float yaw1 = playerYaw + Mth.wrapDegrees(yaw - playerYaw);
        float pitch1 = playerPitch + Mth.wrapDegrees(pitch - playerPitch);

        return new float[] { yaw1, Mth.clamp(pitch1, -90.0f, 90.0f) };
    }

    public Vec3 getRotationVector(float yaw, float pitch)
    {
        float f = pitch * ((float) Math.PI / 180.0f);
        float g = -yaw * ((float) Math.PI / 180.0f);
        float h = Mth.cos(g);
        float i = Mth.sin(g);
        float j = Mth.cos(f);
        float k = Mth.sin(f);
        return new Vec3(i * j, -k, h * j);
    }
}