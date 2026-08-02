package net.shoreline.client.impl.modules.impl;

import net.minecraft.util.Mth;
import net.shoreline.client.api.module.Category;
import net.shoreline.client.api.module.Toggleable;
import net.shoreline.client.impl.Managers;

public class MovementModule extends Toggleable
{
    public MovementModule(String name, String description, Category category)
    {
        super(name, description, category);
    }

    public MovementModule(final String name,
                          final String[] nameAliases,
                          final String description,
                          final Category category)
    {
        super(name, nameAliases, description, category);
    }

    public double getMotionX()
    {
        return mc.player.getDeltaMovement().x;
    }

    public double getMotionY()
    {
        return mc.player.getDeltaMovement().y;
    }

    public double getMotionZ()
    {
        return mc.player.getDeltaMovement().z;
    }

    public void setMotionY(double y)
    {
        mc.player.setDeltaMovement(getMotionX(), y, getMotionZ());
    }

    public void addMotionY(double y)
    {
        mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(0.0, y, 0.0));
    }

    public void setMotionXZ(double x, double z)
    {
        mc.player.setDeltaMovement(x, getMotionY(), z);
    }

    public float[] strafe(float speed)
    {
        float forward = mc.player.input.getMoveVector().y;
        float strafe = mc.player.input.getMoveVector().x;

        float tickDelta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float yaw = Managers.ROTATION.hasClientRotation() ?
                Managers.ROTATION.getClientRotation().getYaw() :
                mc.player.yRotO + (mc.player.getYRot() - mc.player.yRotO) * tickDelta;

        if (forward == 0.0f && strafe == 0.0f)
        {
            return new float[]{0.0f, 0.0f};
        }
        else if (forward != 0.0f)
        {
            if (strafe > 0.0)
            {
                yaw += forward > 0.0 ? -45 : 45;
            }
            else if (strafe < 0.0)
            {
                yaw += forward > 0.0 ? 45 : -45;
            }

            strafe = 0.0f;
            if (forward > 0.0)
            {
                forward = 1.0f;
            }
            else if (forward < 0.0)
            {
                forward = -1.0f;
            }
        }

        float cos = (float) Math.cos(Math.toRadians(yaw));
        float sin = (float) -Math.sin(Math.toRadians(yaw));
        return new float[]
        {
            (forward * speed * sin) + (strafe * speed * cos),
            (forward * speed * cos) - (strafe * speed * sin)
        };
    }

    protected double getAcceleratedSpeed(double baseSpeed,
                                         double maxSpeed,
                                         double accelMaxTime,
                                         long accelTime)
    {
        if (maxSpeed >= baseSpeed)
        {
            return baseSpeed;
        }
        else
        {
            double delta = Mth.clamp((double) (System.currentTimeMillis() - accelTime) / (accelMaxTime * 1000.0), 0.0, 1.0);
            double speed = maxSpeed + (baseSpeed - maxSpeed) * delta;
            return Math.min(speed, baseSpeed);
        }
    }
}