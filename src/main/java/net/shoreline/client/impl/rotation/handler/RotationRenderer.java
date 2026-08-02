package net.shoreline.client.impl.rotation.handler;

import lombok.Getter;
import net.minecraft.util.Mth;
import net.shoreline.client.api.interfaces.Globals;

@Getter
public class RotationRenderer implements Globals
{
    private float yaw, pitch, prevYaw, prevPitch;
    private float yawOffset, prevYawOffset;

    public void update(float yaw, float pitch)
    {
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYawOffset()
    {
        prevYawOffset = yawOffset;
        return yawOffset =  getYawOffset(yaw, prevYawOffset);
    }

    private float getYawOffset(float yaw, float offsetIn)
    {
        float result = offsetIn;
        float offset;

        double xDif = mc.player.getDeltaMovement().x;
        double zDif = mc.player.getDeltaMovement().z;

        if (xDif * xDif + zDif * zDif > 0.0025000002f)
        {
            offset = (float) Mth.atan2(zDif, xDif) * 57.295776f - 90.0f;
            float wrap = Mth.abs(Mth.wrapDegrees(yaw) - offset);
            if (95.0F < wrap && wrap < 265.0F)
            {
                result = offset - 180.0F;
            }
            else
            {
                result = offset;
            }
        }

        if (mc.player.attackAnim > 0.0F)
        {
            result = yaw;
        }

        result = offsetIn + Mth.wrapDegrees(result - offsetIn) * 0.3f;
        offset = Mth.wrapDegrees(yaw - result);

        if (offset < -75.0f)
        {
            offset = -75.0f;
        }
        else if (offset >= 75.0f)
        {
            offset = 75.0f;
        }

        result = yaw - offset;
        if (offset * offset > 2500.0f)
        {
            result += offset * 0.2f;
        }

        return result;
    }
}